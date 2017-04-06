package eltech.bignumbers.numberclasses;

import eltech.bignumbers.util.ExtMath;
import java.util.ArrayList;
import java.util.List;

/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/*
    LIST TO DO:
    1) TODO somewhere direct access to digits array can be avoided by using API.
        Using API is safer, cuz of no ArrayOutOfBounds. Need switch to it.
		Also avoid of digits.size() using -- need method for it.
    2) TODO fix fucking for loops in toString()s -- they HAVE to show zerolengths
 */

public class BigInteger implements Comparable<BigInteger> {

    // ==================
    /// === Constants ===
    // ==================

    private static final char BASE = 10;

    // =========================
    /// === Internal classes ===
    // =========================

    public static class Digit implements Comparable<Digit> {
        private char sval;

	    public Digit() {
            this((char) 0);
        }

	    public Digit(char d) {
            this.set(d);
        }

        private void set(char d) {
            if (d >= BASE) {
                throw new java.lang.IllegalArgumentException(
                        "Expected hexadecimal digit in range [0;" + (int) BASE + "), got " + (int) d);
            }

            this.sval = d;
        }

        private char get() {
            return this.sval;
        }

        @Override
        public int compareTo(Digit digit) {
            return this.sval == digit.sval ? 0 : (this.sval > digit.sval ? 1 : -1);
        }

	    public String toString(){
        	return String.format("%d", (int)this.sval);
	    }
    }

    // ==============
    // === FIELDS ===
    // ==============

    private List<Digit> digits;
    private boolean isNegative;


    // =====================
    /// === Constructors ===
    // =====================

    @SuppressWarnings( "WeakerAccess")
    public BigInteger() {
        this(0);
    }

    public BigInteger(long initer) {
        this.isNegative = initer < 0;

        this.digits = new ArrayList<>();

        int digits_count = (initer == 0 ? 0 : (int) (Math.log(Math.abs(initer)) / Math.log(BASE))) + 1;

	    if(digits_count==0){
		    digits.add(new Digit('\0'));
	    }

        for (int i = 0; i < digits_count; i++) {
            digits.add(new Digit((char) ExtMath.mod(initer, BASE)));
            initer = (long) ExtMath.div(initer, BASE);
        }

    }

	@SuppressWarnings("WeakerAccess")
	public BigInteger(List<Digit> digits, boolean isNegative) {
		this.digits = new ArrayList(digits);
		this.isNegative = isNegative;
	}

	@SuppressWarnings("WeakerAccess")
	public BigInteger(char[] digits, boolean isNegative) {
		this.digits = new ArrayList();
		for(int i=0; i<digits.length; i++){
			//Digit constructor would check if input array is correct
			//TODO do we need to handle it somehow special here?..
			this.digits.add(new Digit(digits[i]));
		}
		this.isNegative = isNegative;
	}

    @SuppressWarnings("WeakerAccess")
    public BigInteger(BigInteger clone) {
        this.isNegative = clone.isNegative();

        this.digits = new ArrayList<>();

        for (int i = 0; i < clone.digits.size(); i++) {
            digits.add(new Digit(clone.getDigitAsByteOrDefault(i)));
        }
    }

    // ===============================
    // === SOME DIGIT MANIPULATING ===
    // ===============================

    private char getDefaultAsByte() {
        return isNegative ? (char) (BASE - 1) : '\0';
    }

    private char getDigitAsByteOrDefault(int n) {
        if (n < digits.size()) {
            return digits.get(n).get();
        } else {
            return isNegative ? (char) (BASE - 1) : '\0';
        }
    }

    private void setDigit(int n, char sval) {
        while (digits.size() <= n) {
            digits.add(new Digit());
        }
        digits.get(n).set(sval);
    }

    private void insertDigit(int n, char sval) {
        if(n>=this.digits.size()){
            this.setDigit(n, sval);
            return;
        }

        digits.add(n, new Digit(sval));
    }

    private void trimLeading() {
        for (int i = this.digits.size() - 1; i > 0 && this.getDigitAsByteOrDefault(i) == this.getDefaultAsByte(); i--) {
            this.digits.remove(i);
        }
    }

    // ====================
    // === CORE ENTRIES ===
    // ====================

    /**
     * Supposed to solve N-1 a.k.a COM_NN_D
     */
    @Override
    public int compareTo(BigInteger arg) {
        if (this.isNegative && !arg.isNegative) {
            return -1;
        }
        if (!this.isNegative && arg.isNegative) {
            return 1;
        }

        int negativesShift = this.isNegative ? -1 : 1; //and so also arg.isNegative is true

        BigInteger thisCpy = new BigInteger(this);
        thisCpy.abs();
        BigInteger argCpy = new BigInteger(arg);
        argCpy.abs();

        //Comparing length of numbers
        if(thisCpy.digits.size() > argCpy.digits.size()){
            return 1*negativesShift; //Consider, *this* number is higher
        }else if(this.digits.size() < arg.digits.size()){
            return -1*negativesShift; //Consider, argument number is higher
        }else{
        //Oh, let's compare digit-by-digit ;(
        for (int i = Math.max(thisCpy.digits.size(), argCpy.digits.size()); i >= 0; i--) {
            int digitsComparsion = thisCpy.getDigitAsByteOrDefault(i) - argCpy.getDigitAsByteOrDefault(i);
            if (digitsComparsion != 0) {
                return digitsComparsion * negativesShift;
            }
        }

        return 0;
        }
    }

    /**
     * Supposed to solve N-2 a.k.a NZER_N_B
     */
    public boolean isZero() {
        return !this.isNegative() && this.digits.size() == 1 && digits.get(0).get() == 0;
    }

	public boolean isOne() {
		return this.digits.size() == 1 && digits.get(0).get() == 1;
	}

    /**
     * Supposed to solve N-3 a.k.a. ADD_1N_N
     */
    @SuppressWarnings("unused")
    public void increment() {
        int i;
        for (i = 0; i < digits.size() && digits.get(i).get() == BASE - 1; i++) {
            this.setDigit(i, '\0');
        }
        if (i >= digits.size() && isNegative) {
            //So, the number became plain zero
            for (int j = digits.size() - 1; j > 0; j--) {
                digits.remove(j);
            }
            isNegative = false;
            return;
        }
        this.setDigit(i, (char) (getDigitAsByteOrDefault(i) + 1));
    }

    /**
     * Supposed to solve:
     * N-3 aka ADD_NN_N
     * N-4 aka SUB_NN_N
     * Z-6 aka ADD_ZZ_Z
     * Z-7 aka SUB_ZZ_Z
     * <p>
     * //TODO it's a bit messy
     */
    public void add(BigInteger arg) {
        if (arg.isZero()) return;

        char buffer = 0;

        int iMax = Math.max(this.digits.size(), arg.digits.size());

        int i;
        for (i = 0; i < iMax; i++) {
            buffer += this.getDigitAsByteOrDefault(i) + arg.getDigitAsByteOrDefault(i);

            //WHY DA FUCK can we need that??// if(i<this.digits.size()) {
            this.setDigit(i, (char) (ExtMath.mod(buffer, BASE)));
            //}
            buffer = (char) ExtMath.div(buffer, BASE);
        }


        //noinspection StatementWithEmptyBody
        if (this.isNegative && arg.isNegative) {
            //Summ keeps being negative
            //Do nothing
        } else if (!(this.isNegative || arg.isNegative)) {
            this.setDigit(i, buffer);
        } else {
            if (buffer != 0) {
                //it became zero
                this.isNegative = false;
                if (this.digits.get(iMax - 1).get() == 0) {
                    for (int j = digits.size() - 1; j > 0 && digits.get(j).get() == 0; j--) {
                        digits.remove(j);
                    }
                }
            } else {
                this.isNegative = true;
            }
        }

        this.trimLeading(); //TODO improve method somehow, no need to external call
    }

    /**
     * Supposed to solve N-5 aka MUL_ND_N
     */
    private void multiply(Digit arg) {

        if (arg.get() == 0) {
            this.digits = new ArrayList();
            this.digits.add(new Digit('\0'));
            this.isNegative = false;
            return;
        }

        char buffer = 0;
        int i; //needed for commented part
        for (i = 0; i < this.digits.size(); i++) {
            buffer += this.getDigitAsByteOrDefault(i) * arg.get();
            this.setDigit(i, (char) ExtMath.mod(buffer, BASE));
            buffer = (char) ExtMath.div(buffer, BASE);
        }

        if(buffer!=0){
	        this.setDigit(this.digits.size(), buffer);
        }

        /*
        int oldbuffer = -1;
        char product = this.getDefaultAsByte();
        while (buffer != oldbuffer || product != this.getDefaultAsByte()) {
            oldbuffer = buffer;
            buffer += this.getDigitAsByteOrDefault(i) * arg.get();
            product = (char) ExtMath.mod(buffer, BASE);
            buffer = (char) ExtMath.div(buffer, BASE);
            this.setDigit(i, product);
            i++;

            //System.out.println("\t"+oldbuffer+" - > "+(int)product);
        }*/
    }

    /**
     * Supposed to solve:
     * N-6 aka MUL_Nk_N
     * Z-8 aka MUL_ZZ_Z
     */
    public void shiftl(int n) {
        if (this.isZero()){
        	return;
        }

        for (int i = 0; i < n; i++) {
            digits.add(0, new Digit());
        }
    }

	public void shiftr(int n) {
		if (this.isZero()){
			return;
		}

		for (int i = 0; i < n && !digits.isEmpty(); i++) {
			digits.remove(0);
		}

		if(digits.isEmpty()){
			setDigit(0, '\0');
		}
	}

    /**
     * Supposed to solve N-7 aka MUL_NN_N
     */
    public void multiply(BigInteger arg) {

        if (this.isZero()) {
            return;
        }

        if (arg.isZero()) {
            this.digits = new ArrayList<>();
            this.digits.add(new Digit('\0'));
            this.isNegative = false;
            return;
        }

        BigInteger argCpy = new BigInteger(arg);

        boolean resultNegative = this.isNegative != argCpy.isNegative;

        this.abs();
        argCpy.abs();

        BigInteger ret = new BigInteger(0);

        for (int i = 0; i < argCpy.digits.size(); i++) {
            Digit digit = argCpy.digits.get(i);
            BigInteger submult = new BigInteger(this);
            submult.multiply(digit);
            submult.shiftl(i);
            ret.add(submult);
            //System.out.println(ret.toString()+"\t"+submult.toString());
        }

        if (resultNegative) {
            ret.toggleSign();
        }

        this.isNegative = ret.isNegative;
        this.digits = ret.digits;

        this.trimLeading();
    }
	/**
	 * Supposed to solve:
	 * N-11 aka IV_NN_N
	 * N-12 aka MOD_NN_N
	 * Z-9 aka DIV_ZZ_Z
	 * Z-10 aka MOD_ZZ_Z
	 */
    public DivModResult<BigInteger> divmod(BigInteger arg) {
        if (arg.isZero()) {
            throw new java.lang.IllegalArgumentException("Can't divide by zero");
        }

	    BigInteger argCpy = new BigInteger(arg);
	    argCpy.abs();
	    BigInteger argCpyNeg = new BigInteger(argCpy);
	    argCpyNeg.toggleSign();

	    BigInteger thisCpy = new BigInteger(this);
	    thisCpy.abs();

        boolean isResultNegative = arg.isNegative != this.isNegative;

        ArrayList<Digit> div = new ArrayList();

        int lengthDiff = thisCpy.digits.size() - argCpy.digits.size();

        argCpy.shiftl(lengthDiff);
	    argCpyNeg.shiftl(lengthDiff);

        char currentDivDigit;
        boolean leadingZero = true;
        for (int i = 0; i <= lengthDiff; i++) {

	        currentDivDigit = 0;

	        while(thisCpy.compareTo(argCpy) >= 0){
	        	currentDivDigit++;
		        thisCpy.add(argCpyNeg);
	        }

            if (currentDivDigit != 0 || !leadingZero) { //Avoiding leading zeroes
	            leadingZero = false;

                div.add(0, new Digit(currentDivDigit));
            }

	        argCpy.shiftr(1);
	        argCpyNeg.shiftr(1);
        }

        if(leadingZero){
        	isResultNegative = false;
        	div.add(0, new Digit('\0'));
        }

        BigInteger tmp = new BigInteger(div, false);

        if(isResultNegative){
            tmp.toggleSign();
        }

        return new DivModResult(tmp, thisCpy);
    }
    /**
     * Supposed to solve Z-1 aka ABS_Z_N
     */
    @SuppressWarnings("WeakerAccess")
    public void abs() {
        if (!isNegative) {
            return;
        }

        toggleSign();
    }

    /**
     * Supposed to solve Z-2 POZ_Z_D
     *
     * @return If number is negative, i.e. <0
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isNegative() {
        return isNegative;
    }

    /**
     * Supposed to solve Z-3 aka MUL_ZM_Z
     */
    public void toggleSign() {
        if (isZero()) {
            return;
        }

        BigInteger ret = new BigInteger(0);

        char buffer = 1;

        for (int i = 0; i < this.digits.size(); i++) {
            char digit = (char) ((BASE - 1) - this.getDigitAsByteOrDefault(i) + buffer);
            buffer = (char) ExtMath.div(digit, BASE);
            ret.setDigit(i, (char) ExtMath.mod(digit, BASE));
        }

        if(buffer != 0){
	        ret.setDigit(this.digits.size(), buffer);
        }

        this.isNegative = !this.isNegative;
        this.digits = ret.digits;
    }

    // ========================
    // === ToString methods ===
    // ========================

    @SuppressWarnings("unused")
    public String toStringUnprime() {
        StringBuilder builder = new StringBuilder();

	    if (isNegative) {
		    builder.append("-");
	    }

        for (int i = digits.size() - 1; i >= 0; i--) {
            builder.append(digits.get(i).toString());
        }

        return builder.toString();
    }

    @SuppressWarnings("unused")
    public String toString() {
        StringBuilder builder = new StringBuilder();

	    if (isNegative) {
		    builder.append("-");
	    }

        BigInteger absThis = new BigInteger(this);
        absThis.abs();
		builder.append(absThis.toStringUnprime());

        return builder.toString();
    }

    public boolean equals(Object arg){
    	if(!this.getClass().equals(arg.getClass())){
    		return false;
	    }

	    BigInteger argBH = (BigInteger) arg;

	    if(this.digits.size() != argBH.digits.size()){
	    	return false;
	    }

	    for(int i=0; i<this.digits.size(); i++){
	    	if(this.getDigitAsByteOrDefault(i) != argBH.getDigitAsByteOrDefault(i)){
	    		return false;
		    }
	    }

    	return true;
    }

    /*public static BigInteger[] generateArrayOfLength(int n){
        ArrayList<BigInteger> digits = new ArrayList<>();
        BigInteger digit = new BigInteger(Long.MAX_VALUE/(3+new Random().nextInt(100)));
        for(int i=0; i<n; i++){
            digits.add(new BigInteger(digit));
            digits.get(i).isNegative = new Random().nextBoolean();
            digit.add(digit);
        }

        //digits.get(n-1).isNegative = new Random().nextBoolean();

        BigInteger[] arrDigits = new BigInteger[digits.size()];
        digits.toArray(arrDigits);

        return arrDigits;
    }*/
}