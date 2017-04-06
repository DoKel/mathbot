package eltech.bignumbers.numberclasses;

/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import eltech.bignumbers.util.BigIntegerMath;

public class BigFraction implements Comparable<BigFraction> {

    // ==============
    // === FIELDS ===
    // ==============

    private BigInteger numerator;
    private BigInteger denominator;


    // =====================
    /// === Constructors ===
    // =====================

    public BigFraction(BigInteger num, BigInteger den){
        if(den.isNegative()){
            throw new java.lang.IllegalArgumentException("Denominator must be positive, got "+ den);
        }
        if(den.isZero()){
            throw new java.lang.IllegalArgumentException("Denominator must be positive, got zero");
        }

        this.numerator = num;
        this.denominator = den;

        this.reduce();
    }

	public BigFraction(BigInteger numerator) {
		this (numerator, new BigInteger(1));
	}

	public BigFraction(long numerator, long denominator) {
		this (new BigInteger(numerator), new BigInteger(denominator));
	}

	public BigFraction(long numerator) {
		this(numerator, 1);
	}

	public BigFraction(BigFraction arg) {
		this.numerator = new BigInteger(arg.numerator);
		this.denominator = new BigInteger(arg.denominator);
	}

    // ===========================
    // === SOME STATIC METHODS ===
    // ===========================

    static BigFraction getZeroObject() {
        return new BigFraction(new BigInteger(), new BigInteger(1));
    }

    // ===========================
    // === SOME USEFUL METHODS ===
    // ===========================


    @Override
    public int compareTo(BigFraction arg) {
        if(!this.numerator.isNegative() && arg.numerator.isNegative()){
            return 1;
        }
        if(this.numerator.isNegative() && !arg.numerator.isNegative()){
            return -1;
        }

        BigInteger lcm = BigIntegerMath.getLCM(this.denominator, arg.denominator);
        BigInteger thisMult = lcm.divmod(this.denominator).getDivResult();
        BigInteger argMult = lcm.divmod(arg.denominator).getDivResult();

        BigInteger argNum = new BigInteger(arg.numerator);
        argNum.multiply(argMult);
        BigInteger thisNum = new BigInteger(this.numerator);
        argNum.multiply(thisMult);

        return thisNum.compareTo(argNum);
    }

    public boolean isZero(){
        return numerator.isZero();
    }

    public boolean isOne(){
        return numerator.compareTo(denominator) == 0;
    }

    public void toggleSign(){
        this.numerator.toggleSign();
    }

    public void abs(){
        this.numerator.abs();
    }

    public boolean isNegative(){
        return this.numerator.isNegative();
    }

    public BigInteger getDenominator() {
        return new BigInteger(denominator);
    }

    // ====================
    // === CORE ENTRIES ===
    // ====================

    /**
     * Supposed to solve Q-1 aka RED_Q_Q
     */
    private void reduce(){
        BigInteger gcd = BigIntegerMath.getGCD(numerator, denominator);

        this.numerator = numerator.divmod(gcd).getDivResult();
        this.denominator = denominator.divmod(gcd).getDivResult();
    }

    /**
     * Supposed to solve Q-2 aka INT_Q_B
     */
    public boolean isInteger(){
        return this.denominator.equals(new BigInteger(1));
    }

    /**
     * Supposed to solve Q-4 aka TRANS_Q_Z
     */
    public BigInteger getNumerator(){
        return new BigInteger(this.numerator);
    }

    /**
     * Supposed to solve:
     *      Q-5 aka ADD_QQ_Q
     *      Q-6 aka SUB_QQ_Q
     */
    public void add(BigFraction arg){
        BigInteger lcm = BigIntegerMath.getLCM(this.denominator, arg.denominator);
        BigInteger thisMult = lcm.divmod(this.denominator).getDivResult();
        BigInteger argMult = lcm.divmod(arg.denominator).getDivResult();

        BigInteger argNum = new BigInteger(arg.numerator);
        argNum.multiply(argMult);

        this.numerator.multiply(thisMult);
        this.numerator.add(argNum);
        this.denominator = lcm;

        //TODO probably, the algo is reducing number by itself.
        this.reduce();
    }

    /**
     *  Supposed to solve Q-7 aka MUL_QQ_Q
     */
    public void multiply(BigFraction arg){
        if(isZero()){
            return;
        }

        if(arg.isZero()){
            this.numerator = new BigInteger(0);
            this.denominator = new BigInteger(1);
            return;
        }

        this.numerator.multiply(arg.numerator);
        this.denominator.multiply(arg.denominator);

        this.reduce();
    }

    /**
     *  Supposed to solve Q-8 aka DIV_QQ_Q
     */
    public void divide(BigFraction arg){
        if(arg.isZero()){
            throw new java.lang.IllegalArgumentException("Can't divide by zero");
        }

        this.numerator.multiply(arg.denominator);
        this.denominator.multiply(arg.numerator);

        if(denominator.isNegative()){
            denominator.toggleSign();
            numerator.toggleSign();
        }

        this.reduce();
    }

    // ========================
    // === ToString methods ===
    // ========================

    public String toString(){
        return toString(true);
    }


    public String toString(boolean inPrime){
	    StringBuilder builder = new StringBuilder();

	    boolean isInteger = !denominator.isOne();

	    if(isInteger) {
		    builder.append("( ");
	    }

	    builder.append(inPrime?
			    numerator.toString():
			    numerator.toStringUnprime()
	    );

	    if(isInteger) {
		    builder.append(" / ");
		    builder.append(denominator.toString()); //Assume denominator is positive anyway
		    builder.append(" )");
	    }

	    return builder.toString();
    }

}
