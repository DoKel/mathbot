package eltech.bignumbers.util;

import eltech.bignumbers.numberclasses.BigInteger;
import eltech.bignumbers.numberclasses.DivModResult;
import eltech.bignumbers.numberclasses.BigFraction;
import eltech.bignumbers.numberclasses.BigPolynomial;

/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class BigPolynomialMath {
    /**
     * Supposed P-11 aka GCF_PP_P
     */
    public static BigPolynomial getGCD(BigPolynomial firstPBH, BigPolynomial secondPBH){
        BigPolynomial a = new BigPolynomial(firstPBH);
        BigPolynomial b = new BigPolynomial(secondPBH);

        //Quick answer if zeroes

        if(a.isZero() && !b.isZero()){
            return b;
        }else if(!a.isZero() && b.isZero()){
            return a;
        }else if(a.isZero() && b.isZero()){
            //Same logic, as in BigIntegerMath
            BigFraction[] coefs = new BigFraction[]{new BigFraction(new BigInteger(1), new BigInteger(1))};
            return new BigPolynomial(coefs);
        }

	    //Quick answer if constants

	    if(a.getDeg()==0 && b.getDeg()!=0){
		    return a;
	    }else if(a.getDeg()!=0 && b.getDeg()==0){
		    return b;
	    }else if(a.getDeg()==0 && b.getDeg()==0){
		    //Same logic, as in BigIntegerMath
		    BigFraction[] coefs = new BigFraction[]{new BigFraction(new BigInteger(1), new BigInteger(1))};
		    return new BigPolynomial(coefs);
	    }

        if(a.getDeg() < b.getDeg()){
            BigPolynomial tmp = a;
            a = b;
            b = tmp;
        }

        while(b.getDeg()>0){
            DivModResult<BigPolynomial> res = a.divmod(b);
            a = b;
            b = res.getModResult();
        }

        return a;
    }
}
