package org.mbte.groovypp.compiler

public class CompareTest extends GroovyShellTestCase{
  void testMath () {
    shell.evaluate("""
      @Compile
      def u () {
        assert (10l == 10)
        assert (10 > 5G)
        assert !(10 < 5.0f)
        assert (10 >= 5)
        assert !(10d <= 5)
        assert (10l != 5d)
        assert (10g != 5d)
        assert (10  >  5d)
      }
      u ()
    """)
  }

  void testIntegerConstants () {
    shell.evaluate("""
      @Compile
      def u () {
          assert (10  >  5)
          assert (5  <  10)
          assert (4  >=  4)
          assert (-4  >=  -5)
          assert (4  >=  4)
          assert (4  !=  5)

          assert (4  <=  4)
          assert (-5  <=  -4)
          assert (4  <=  4)

          assert (5 == 5)
          assert (-5 == -5)


      }
      u ()
    """)
  }

    void testDoubleConstants () {
    shell.evaluate("""
      @Compile
      def u () {
          assert (10.0  >  5.0)
          assert (5.0  <  10.0)
          assert (4.0  >=  4.0)
          assert (-4.0  >=  -5.0)
          assert (4.0  >=  4.0)
          assert (4.0  !=  5.0)

          assert (4.0  <=  4.0)
          assert (-5.0  <=  -4.0)
          assert (4.0  <=  4.0)

          assert (5.0 == 5.0)
          assert (-5.0 == -5.0)

          // Java seems to treat NaN comparison differently
          assert (1.0 < Double.NaN)
          assert (1.0 < Double.POSITIVE_INFINITY)
          assert (1.0 > Double.NEGATIVE_INFINITY)
          assert (0.0/0.0 == Double.NaN)
          assert (1.0/0.0 == Double.POSITIVE_INFINITY)
          assert (-1.0/0.0 == Double.NEGATIVE_INFINITY)
          assert (-1.0/-0.0 == Double.POSITIVE_INFINITY)
      }
      u ()
    """)
  }

  void testMixedTypeConstants () {
    shell.evaluate("""
      @Compile
      def u () {
          assert (10L  >  5.0)
          assert (5.0  <  10L)
          assert (4L  >=  4d)
          assert ((byte)-4  >=  -5L)
      }
      u ()
    """)
  }

  void testAutounboxing () {
    shell.evaluate("""
      @Compile
      def u () {
          assert (new Double(10)  >  new Double(5))
          assert (new Double(5)  <  new Integer(10))
          assert (new Double(10)  ==  new Double(10))
          assert (new Integer(10)  ==  new Double(10))
      }
      u ()
    """)
  }

  void testNegativeZeros () {
    shell.evaluate("""
      @Compile
      def u () {
        assert 0.0f != -(0.0f)
        assert 0.0f == -0.0f

        assert 0.0d != -(0.0d)
        assert 0.0d != -0.0d
      }
      u ()
    """)
  }

  void testClassTypes () {
    shell.evaluate("""
      @Compile
      def u () {
        assert (Float.TYPE == float)
        assert (Double.TYPE == double)

        // Doesn't work here
        assert (1.2d.class == Double)

        
      }
      u ()
    """)
  }
}