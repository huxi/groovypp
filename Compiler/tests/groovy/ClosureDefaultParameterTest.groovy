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



package groovy

/**
 * @author <a href="mailto:jstrachan@protique.com">James Strachan</a>
 * @version $Revision: 9213 $
 */
class ClosureDefaultParameterTest extends GroovyShellTestCase {

    void testClosureWithDefaultParams() {
        shell.evaluate("""
          @Typed
          def u() {
            def block = {a = 123, b = 456 -> println "value of a = \$a and b = \$b" }

            block = { Integer a = 123, String b = "abc" ->
                      println "value of a = \$a and b = \$b"; return "\$a \$b".toString() }

            assert block.call(456, "def") == "456 def"
            assert block.call() == "123 abc"
            assert block(456) == "456 abc"
            assert block(456, "def") == "456 def"
          }
          u();
        """
        )
    }

    void testClosureWithDefaultParamFromOuterScope() {
        shell.evaluate("""
          @Typed
          def u() {
            def y = 555
            def boo = {x = y -> x}
            assert boo() == y
            assert boo(1) == 1
          }
          u();
        """
        )
    }

}

