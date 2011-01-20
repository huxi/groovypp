/*
 * Copyright 2009-2011 MBTE Sweden AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */





package org.mbte.groovypp.compiler.Issues

public class Issue15Test extends GroovyShellTestCase {
    void testNpe () {
        shell.evaluate """
package widefinder

@Typed
class Start
{
    static final INT     LINES     = new INT();

    static class INT
    {
        int  counter = 0;
        void increment(){ this.counter++ }
        void decrement(){ --this.counter }
    }

    public static void main ( String[] args )
    {
       def i = new INT ()
       (0..10).each {
          assert  1 == ++i.counter
          assert  0 == --i.counter
          assert  0 ==   i.counter--
          assert -1 ==   i.counter++

          i.counter = 0
          i.increment ()
          i.decrement ()
       }
       assert i.counter == 0
    }
}        """
    }
}