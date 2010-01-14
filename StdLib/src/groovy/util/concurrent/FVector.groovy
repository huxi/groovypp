/**
 Copyright (c) 2007-2008, Rich Hickey
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above
 copyright notice, this list of conditions and the following
 disclaimer in the documentation and/or other materials provided
 with the distribution.

 * Neither the name of Clojure nor the names of its contributors
 may be used to endorse or promote products derived from this
 software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 * */

package groovy.util.concurrent

/*
  A straight port of Clojure's <code>PersistentVector</code> class.
  @author Daniel Spiewak
  @author Rich Hickey
*/
@Typed
class FVector<T> implements Iterable<T> {
  int length
  int shift
  Object[] root
  T[] tail

  private def FVector(int length, int shift, Object[] root, T[] tail) {
    this.length = length
    this.shift = shift
    this.root = root
    this.tail = tail
  }

  static FVector<Object> emptyVector = new FVector(0, 5, new Object[0], new Object[0])

  private int tailOff () { length - tail.length }

  T getAt(int i) {
    if (i >= 0 && i < length) {
      if (i >= tailOff()) {
        return tail[i & 0x1f]
      } else {
        def arr = (shift..<0).step(5).foldLeft(root) {level, arr -> (Object[])arr[(i >>> level) & 0x1f] }
        return (T)arr[i & 0x1f]
      }
    } else {
      throw new IndexOutOfBoundsException("Tried to access FVector out of its bounds: " + i)
    }
  }

  FVector<T> set(int i, T obj) {
    if (i >= 0 && i < length) {
      if (i >= tailOff()) {
        def newTail = new T[tail.length]
        System.arraycopy tail, 0, newTail, 0, tail.length
        newTail[i] = obj
        return new FVector<T> (length, shift, root, newTail)
      } else {
        return new FVector<T> (length, shift, doAssoc(shift, root, i, obj), tail)
      }
    } else if (i == length) {
      return this + obj
    } else {
      throw new IndexOutOfBoundsException("Tried to update FVector out of its bounds: " + i)
    }
  }

  private Object[] doAssoc(int level, Object[] arr, int i, T obj) {
    def ret = new T[arr.length]
    System.arraycopy arr, 0, ret, 0, arr.length
    if (level == 0) {
      ret[i & 0x1f] = obj
    } else {
      def subi = (i >>> level) & 0x1f
      ret[subi] = doAssoc(level - 5, (Object[])arr[subi], i, obj)
    }
    ret
  }

  FVector<T> addAll (Iterable<T> other) {
    other.foldLeft(this) {e, vec -> vec + e}
  }

  FVector<T> plus(T obj) {
    if (tail.length < 32) {
      def newTail = new T[tail.length + 1]
      System.arraycopy tail, 0, newTail, 0, tail.length
      newTail[tail.length] = obj
      return new FVector<T> (length + 1, shift, root, newTail)
    } else {
      def pushed = pushTail(shift - 5, root, tail)
      Object[] newRoot = pushed.first
      T expansion = pushed.second
      def newShift = shift
      if (expansion) {
        newShift += 5
        newRoot = [newRoot, expansion]
      }
      T[] newTail = [obj]
      return new FVector<T> (length + 1, newShift, newRoot, newTail)
    }
  }

  private Pair<Object[], Object> pushTail(int level, Object[] arr, T[] tailNode) {
    def newChild
    if (level == 0) newChild = tailNode else {
      def rec = pushTail(level - 5, (Object[])arr[arr.length - 1], tailNode)
      def subexp = rec.second
      if (subexp != null) newChild = subexp else {
        def ret = new Object[arr.length]
        System.arraycopy arr, 0, ret, 0, arr.length
        ret[arr.length - 1] = rec.first
        return [ret, null]
      }
    }
    if (arr.length == 32) {
      return [arr, (Object[])[newChild]]
    } else {
      def ret = new Object[arr.length + 1]
      System.arraycopy arr, 0, ret, 0, arr.length
      ret[arr.length] = newChild
      return [ret, null]
    }
  }

  Pair<T, FVector<T>> pop() {
    if (length == 0) {
      throw new IllegalStateException("Cannot pop from empty vector")
    } else if (length == 1) {
      return [tail[0], emptyVector]
    } else if (tail.length > 1) {
      def newTail = new T[tail.length -1]
      System.arraycopy tail, 0, newTail, 0, newTail.length
      return [tail[tail.length - 1], new FVector<T>(length - 1, shift, root, newTail)]
    } else {
      def popped = popTail(shift - 5, root)
      def newRoot = popped.first
      def pTail = popped.second
      if (newRoot == null) newRoot = new Object[0]
      def newShift = shift
      if (shift > 5 && newRoot.length == 1) {
        newRoot = (Object[])newRoot[0]
        newShift -= 5
      }
      return [tail[0], new FVector<T>(length - 1, newShift, newRoot, (T[])pTail)]
    }
  }

  private Pair<Object[], Object> popTail(int shift, Object[] arr) {
    def newTail
    if (shift  > 0) {
      def popped = popTail(shift - 5, (Object[])arr[arr.length - 1])
      def newChild = popped.first
      def subPTail = popped.second
      if (newChild != null) {
        def ret = new Object[arr.length]
        System.arraycopy arr, 0, ret, 0, arr.length
        ret[arr.length - 1] = newChild
        return [ret, subPTail]
      }
      newTail = subPTail
    } else {
      newTail = arr[arr.length - 1]
    }
    if (arr.length == 1) {
      return [null, newTail]
    } else {
      def ret = new Object[arr.length - 1]
      System.arraycopy arr, 0, ret, 0, ret.length
      return [ret, newTail]
    }
  }

  Iterator<T> iterator() {
    def rec = (shift..<0).step(5).foldLeft(root.iterator()) { level, iter -> iter.map{ ((Object[])it).iterator() }.flatten() }
    rec.followedBy(tail.iterator())
  }
}