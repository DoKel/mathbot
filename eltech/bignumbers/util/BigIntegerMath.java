package eltech.bignumbers.util;

/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import eltech.bignumbers.numberclasses.BigInteger;
import eltech.bignumbers.numberclasses.DivModResult;

public class BigIntegerMath {

    /**
     * Supposed N-13 aka GCF_NN_N
     */
    public static BigInteger getGCD(BigInteger firstBHN, BigInteger secondBHN){
        BigInteger a = new BigInteger(firstBHN);
        a.abs();
        BigInteger b = new BigInteger(secondBHN);
        b.abs();

        if(a.isZero() && !b.isZero()){
            return b;
        }else if(!a.isZero() && b.isZero()){
            return a;
        }else if(a.isZero() && b.isZero()){
            //Is it 1 rly?
            //At least accertion of gcd(whatever, 0) = 0 will be true
            return new BigInteger(1);
        }


        if(a.compareTo(b) == 0){
            return a;
        }

        if(a.compareTo(b) < 0){
            BigInteger tmp = a;
            a = b;
            b = tmp;
        }

        while(!b.isZero()){
            DivModResult<BigInteger> res = a.divmod(b);
            a = b;
            b = res.getModResult();
        }

        return a;
    }


    /**
     * Supposed N-14 aka LCM_NN_N
     */
    public static BigInteger getLCM(BigInteger firstBHN, BigInteger secondBHN){
        BigInteger mult = new BigInteger(firstBHN);
        mult.multiply(secondBHN);
        BigInteger gcd = getGCD(firstBHN, secondBHN);

        return mult.divmod(gcd).getDivResult();
    }
}
