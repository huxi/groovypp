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

public class Issue191Test extends GroovyShellTestCase {
    void testImmutableWithBigDecimal() {
        shell.evaluate """
            @Typed(debug=true)
            @Immutable class AccountV1 {
                BigDecimal balance
                String customer
                
                void deposit(BigDecimal amount) { }
                
                static main(args) {}
            }
            assert AccountV1 != null //cause the class to load to check VerifyError
        """
    }

    void testImmutableWithBigInteger() {
        shell.evaluate """
            @Typed
            @Immutable class AccountV2 {
                BigInteger balance

                void deposit(BigInteger amount) {    }
                
                static main(args) {}
            }
            assert AccountV2 != null //cause the class to load to check VerifyError
        """
    }

    void testBigDecimalWithoutImmutable() {
        shell.evaluate """
            @Typed package p
            class AccountV3 {
                BigDecimal balance
                String customer

                def AccountV3(Map args) {
                    balance = args.get('balance')
                }
                static main(args) {}
            }
            assert AccountV3 != null //cause the class to load to check VerifyError
        """
    }
}
