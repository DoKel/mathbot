package eltech.bignumbers.util;

/**
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class ExtMath {

    public static double mod(double a, double b){
        double q = Math.floor(a/b);
        return a - q*b;
    }

    public static double div(double a, double b){
        return Math.floor(a/b);
    }

}
