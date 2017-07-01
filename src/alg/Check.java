package alg;

import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * check coverage
 */
public class Check {

  // results
  private String coverSize;
  private String[] coverSta;
  private double[][] coverArray;   // t-way * suite size
  // info
  private int parameter;
  private int[] value;
  private ArrayList<Integer> tway;
  private int[][] CA;
  private int CAsize;
  private ArrayList<SubInfo> subset;

  public Check(int par, final int[] val, ArrayList<Integer> t, ArrayList<SubInfo> sub, final ArrayList<int[]> ca) {
    parameter = par;
    value = new int[par];
    System.arraycopy(val, 0, value, 0, par);
    tway = new ArrayList<>();
    tway.addAll(t);

    subset = sub;

    CA = new int[ca.size()][];
    CAsize = ca.size();
    for (int k = 0; k < ca.size(); k++) {
      // construct CA
      int[] get = ca.get(k);
      CA[k] = new int[par];
      System.arraycopy(get, 0, CA[k], 0, par);
    }

    // the size of test suite
    coverSize = String.valueOf(ca.size());
    coverSta = new String[ca.size()];

    // the coverage sequence for each strength
    coverArray = new double[t.size()][ca.size()];
  }

  public String getCoverSize() {
    return this.coverSize;
  }

  public String[] getCoverSta() {
    return this.coverSta;
  }

  public double[][] getCoverArray() {
    return this.coverArray;
  }


  public int cal_combine(int n, int m) {
    int ret = 1;
    int p = n;
    for (int x = 1; x <= m; x++, p--) {
      ret = ret * p;
      ret = ret / x;
    }
    return ret;
  }

  public int cal_val2num(final int[] pos, final int[] val, int t, final int[] value) {
    int com = 1;
    int ret = 0;
    for (int k = t - 1; k >= 0; k--) {
      ret += com * val[k];
      com = com * value[pos[k]];
    }
    return ret;
  }

  // check coverage for each t-column
  // a = the index of testing, ty = t-way
  private double checkCoverage(int a, int ty) {
    BigDecimal CountAll = new BigDecimal("0");
    BigDecimal CountCov = new BigDecimal("0");
    long[] fit = new long[CAsize];    // # combinations covered by each test
    for (int k = 0; k < CAsize; k++)
      fit[k] = 0;

    // for each combination of parameters
    int[] pos = new int[ty];
    int[] pos_max = new int[ty];
    for (int k = 0; k < ty; k++) {
      pos[k] = k;
      pos_max[k] = parameter - ty + k;
    }
    int end = ty - 1, ptr;
    int[] val = new int[ty];

    for (int column = 0; column < cal_combine(parameter, ty); column++) {
      // total number combinations to be covered
      int allneedcov = 1;
      for (int k = 0; k < ty; k++)
        allneedcov = allneedcov * value[pos[k]];
      CountAll = CountAll.add(new BigDecimal(allneedcov));
      int allcovered = allneedcov;

      // covered or not
      int[] count = new int[allneedcov];
      for (int k = 0; k < allneedcov; k++)
        count[k] = 0;

      // for each row
      for (int row = 0; row < CAsize; row++) {
        for (int k = 0; k < ty; k++)
          val[k] = CA[row][pos[k]];
        int index = cal_val2num(pos, val, ty, value);
        if (count[index] == 0) {
          count[index] = 1;
          allcovered--;
          fit[row]++;
        }
      }

      CountCov = CountCov.add(new BigDecimal(allneedcov).subtract(new BigDecimal(allcovered)));

      // to the next combination of parameters
      pos[end] = pos[end] + 1;
      ptr = end;
      while (ptr > 0) {
        if (pos[ptr] > pos_max[ptr]) {
          pos[ptr - 1] = pos[ptr - 1] + 1;
          ptr--;
        } else {
          break;
        }
      }
      if (pos[ptr] <= pos_max[ptr]) {
        for (int i = ptr + 1; i < ty; i++) {
          pos[i] = pos[i - 1] + 1;
        }
      }
    }  // end for

    BigDecimal tp = new BigDecimal("0");
    for (int k = 0; k < CAsize; k++) {
      tp = tp.add(new BigDecimal(fit[k]));
      BigDecimal re = tp.divide(CountAll, 4, BigDecimal.ROUND_HALF_DOWN);
      coverArray[a][k] = re.doubleValue();
    }

    CountCov = CountCov.divide(CountAll, 4, BigDecimal.ROUND_HALF_DOWN);
    return CountCov.doubleValue();
  }

  // check coverage for sub column
  private void checksubcoverage() {
    for (int id = 0; id < subset.size(); id++) {
      int subtway = subset.get(id).getTway();
      int[] subpos = subset.get(id).getPosition();
      int sublength = subset.get(id).getLength();

      long CountAll = 0;
      long CountCov = 0;

      int[] pos = new int[subtway];
      int[] pos_max = new int[subtway];
      int[] pos_real = new int[subtway];
      for (int k = 0; k < subtway; k++) {
        pos[k] = k;
        pos_max[k] = sublength - subtway + k;
      }
      int end = subtway - 1, ptr;
      int[] val_real = new int[subtway];

      for (int column = 0; column < cal_combine(sublength, subtway); column++) {
        for (int k = 0; k < subtway; k++)
          pos_real[k] = subpos[pos[k]];

        int allneedcov = 1;
        for (int k = 0; k < subtway; k++) {
          allneedcov = allneedcov * value[pos_real[k]];
        }
        CountAll += allneedcov;
        int allcovered = allneedcov;

        int[] count = new int[allneedcov];
        for (int k = 0; k < allneedcov; k++) {
          count[k] = 0;
        }

        for (int row = 0; row < CAsize; row++) {
          for (int k = 0; k < subtway; k++) {
            val_real[k] = CA[row][pos_real[k]];
          }
          int index = cal_val2num(pos_real, val_real, subtway, value);
          if (count[index] == 0) {
            count[index] = 1;
            allcovered--;
          }
        }

        CountCov += allneedcov - allcovered;

        pos[end] = pos[end] + 1;
        ptr = end;
        while (ptr > 0) {
          if (pos[ptr] > pos_max[ptr]) {
            pos[ptr - 1] = pos[ptr - 1] + 1;
            ptr--;
          } else {
            break;
          }
        }
        if (pos[ptr] <= pos_max[ptr]) {
          for (int i = ptr + 1; i < subtway; i++) {
            pos[i] = pos[i - 1] + 1;
          }
        }
      }  // end for each column

      // coverage
      double cov = (double) CountCov / (double) CountAll;
      System.out.println("sub " + id + " coverage: " + cov);
      subset.get(id).setCoverage(cov);
    } // end for each
  }


  public void run() {
    System.out.println("--------------check begin--------------");

    // check main coverage
    for (int t = 0; t < tway.size(); t++) {
      int ty = tway.get(t);

      long start = System.currentTimeMillis();
      double cov = checkCoverage(t, ty);
      long end = System.currentTimeMillis();

      coverSta[t] = String.format("%.4f", cov);
      System.out.println(ty + "-way coverage = " + cov + " , time cost = " + (end - start) / 1000);
    }

    // check sub coverage
    if (!subset.isEmpty()) {
      checksubcoverage();
    }

    System.out.println("--------------check end--------------");
  }
}