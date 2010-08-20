/*
 * Copyright 2009-2010 MBTE Sweden AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */





package org.mbte.groovypp.compiler.Issues

import static groovy.util.test.CompileTestSupport.shouldNotCompile

public class Issue56Test extends GroovyShellTestCase {
    void testMe () {
        shouldNotCompile("""
                @Typed
                class Test {
                    static main(String[] args) {
                        foo(new Date())
                    }
                    static foo(Integer x) {
                        println "foo(Integer) called - \$x"
                    }
                }
            """)
    }
}