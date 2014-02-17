/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.weka;

/**
 *
 * @author ark0n3
 */
class WekaClassifier {

  public static double classify(Object[] i)
    throws Exception {

    double p = Double.NaN;
    p = WekaClassifier.N4669b7fe0(i);
    return p;
  }
  static double N4669b7fe0(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 0.09244422) {
    p = WekaClassifier.N46aea8cf1(i);
    } else if (((Double) i[4]).doubleValue() > 0.09244422) {
    p = WekaClassifier.N2f78743b22(i);
    } 
    return p;
  }
  static double N46aea8cf1(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() <= 6.73728665) {
    p = WekaClassifier.N74ccd2492(i);
    } else if (((Double) i[7]).doubleValue() > 6.73728665) {
      p = 1;
    } 
    return p;
  }
  static double N74ccd2492(Object []i) {
    double p = Double.NaN;
    if (i[18] == null) {
      p = 0;
    } else if (((Double) i[18]).doubleValue() <= 5.03210449) {
    p = WekaClassifier.N3301f2873(i);
    } else if (((Double) i[18]).doubleValue() > 5.03210449) {
    p = WekaClassifier.N1efde7ba21(i);
    } 
    return p;
  }
  static double N3301f2873(Object []i) {
    double p = Double.NaN;
    if (i[16] == null) {
      p = 0;
    } else if (((Double) i[16]).doubleValue() <= 4.35012817) {
    p = WekaClassifier.N44d9973a4(i);
    } else if (((Double) i[16]).doubleValue() > 4.35012817) {
    p = WekaClassifier.N234f79cb18(i);
    } 
    return p;
  }
  static double N44d9973a4(Object []i) {
    double p = Double.NaN;
    if (i[16] == null) {
      p = 1;
    } else if (((Double) i[16]).doubleValue() <= -4.80001831) {
    p = WekaClassifier.N295784265(i);
    } else if (((Double) i[16]).doubleValue() > -4.80001831) {
    p = WekaClassifier.N30a4effe6(i);
    } 
    return p;
  }
  static double N295784265(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 0.07495954) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() > 0.07495954) {
      p = 1;
    } 
    return p;
  }
  static double N30a4effe6(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() <= 2.0175021) {
    p = WekaClassifier.N1c8825a57(i);
    } else if (((Double) i[6]).doubleValue() > 2.0175021) {
      p = 0;
    } 
    return p;
  }
  static double N1c8825a57(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() <= 0.06689263) {
      p = 0;
    } else if (((Double) i[4]).doubleValue() > 0.06689263) {
    p = WekaClassifier.N2e5f82458(i);
    } 
    return p;
  }
  static double N2e5f82458(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() <= -2.13384169) {
    p = WekaClassifier.N6197cc9(i);
    } else if (((Double) i[6]).doubleValue() > -2.13384169) {
    p = WekaClassifier.N67d479cf12(i);
    } 
    return p;
  }
  static double N6197cc9(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 0.11664106) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() > 0.11664106) {
    p = WekaClassifier.N734d24610(i);
    } 
    return p;
  }
  static double N734d24610(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 1.16516724) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() > 1.16516724) {
    p = WekaClassifier.N1cd8f55c11(i);
    } 
    return p;
  }
  static double N1cd8f55c11(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() <= 3.69149364) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() > 3.69149364) {
      p = 1;
    } 
    return p;
  }
  static double N67d479cf12(Object []i) {
    double p = Double.NaN;
    if (i[12] == null) {
      p = 0;
    } else if (((Double) i[12]).doubleValue() <= 1.16480611) {
    p = WekaClassifier.N2e893a4a13(i);
    } else if (((Double) i[12]).doubleValue() > 1.16480611) {
      p = 1;
    } 
    return p;
  }
  static double N2e893a4a13(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() <= 1.94775391) {
    p = WekaClassifier.N3351e82414(i);
    } else if (((Double) i[7]).doubleValue() > 1.94775391) {
      p = 1;
    } 
    return p;
  }
  static double N3351e82414(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 0;
    } else if (((Double) i[7]).doubleValue() <= 0.49348291) {
    p = WekaClassifier.N465fadce15(i);
    } else if (((Double) i[7]).doubleValue() > 0.49348291) {
      p = 0;
    } 
    return p;
  }
  static double N465fadce15(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 0;
    } else if (((Double) i[6]).doubleValue() <= 1.49167263) {
    p = WekaClassifier.N338bd37a16(i);
    } else if (((Double) i[6]).doubleValue() > 1.49167263) {
      p = 1;
    } 
    return p;
  }
  static double N338bd37a16(Object []i) {
    double p = Double.NaN;
    if (i[16] == null) {
      p = 0;
    } else if (((Double) i[16]).doubleValue() <= 2.56746054) {
    p = WekaClassifier.N20e9090617(i);
    } else if (((Double) i[16]).doubleValue() > 2.56746054) {
      p = 1;
    } 
    return p;
  }
  static double N20e9090617(Object []i) {
    double p = Double.NaN;
    if (i[2] == null) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() <= 0.1076042) {
      p = 0;
    } else if (((Double) i[2]).doubleValue() > 0.1076042) {
      p = 1;
    } 
    return p;
  }
  static double N234f79cb18(Object []i) {
    double p = Double.NaN;
    if (i[18] == null) {
      p = 1;
    } else if (((Double) i[18]).doubleValue() <= 1.89120483) {
    p = WekaClassifier.N36c5108919(i);
    } else if (((Double) i[18]).doubleValue() > 1.89120483) {
      p = 0;
    } 
    return p;
  }
  static double N36c5108919(Object []i) {
    double p = Double.NaN;
    if (i[17] == null) {
      p = 1;
    } else if (((Double) i[17]).doubleValue() <= 4.78348624) {
    p = WekaClassifier.N43c0ae7620(i);
    } else if (((Double) i[17]).doubleValue() > 4.78348624) {
      p = 1;
    } 
    return p;
  }
  static double N43c0ae7620(Object []i) {
    double p = Double.NaN;
    if (i[18] == null) {
      p = 1;
    } else if (((Double) i[18]).doubleValue() <= -0.27255249) {
      p = 1;
    } else if (((Double) i[18]).doubleValue() > -0.27255249) {
      p = 0;
    } 
    return p;
  }
  static double N1efde7ba21(Object []i) {
    double p = Double.NaN;
    if (i[14] == null) {
      p = 1;
    } else if (((Double) i[14]).doubleValue() <= -2.64816284) {
      p = 1;
    } else if (((Double) i[14]).doubleValue() > -2.64816284) {
      p = 0;
    } 
    return p;
  }
  static double N2f78743b22(Object []i) {
    double p = Double.NaN;
    if (i[8] == null) {
      p = 1;
    } else if (((Double) i[8]).doubleValue() <= 1.17050886) {
      p = 1;
    } else if (((Double) i[8]).doubleValue() > 1.17050886) {
    p = WekaClassifier.Nd16e5d623(i);
    } 
    return p;
  }
  static double Nd16e5d623(Object []i) {
    double p = Double.NaN;
    if (i[6] == null) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() <= 1.74288662) {
      p = 1;
    } else if (((Double) i[6]).doubleValue() > 1.74288662) {
      p = 0;
    } 
    return p;
  }
}

