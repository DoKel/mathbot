package eltech.bignumbers.numberclasses;

import eltech.bignumbers.util.BigIntegerMath;
import eltech.bignumbers.util.BigPolynomialMath;

import java.util.ArrayList;

/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

//TODO deny zero alphas

public class BigPolynomial implements Comparable<BigPolynomial> {
	// ==============
	// === FIELDS ===
	// ==============

	private ArrayList<BigFraction> coefs;
	private BigFraction alphaMultiplier;

	// =====================
	/// === Constructors ===
	// =====================

	public BigPolynomial(ArrayList<BigFraction> coefs){
		this(coefs, new BigFraction(1));
	}

	public BigPolynomial(BigFraction[] coefs){
		this(coefs, new BigFraction(1));
	}

	public BigPolynomial(ArrayList<BigFraction> coefs, BigFraction alphaMultiplier){
		this.coefs = coefs;

		if(this.coefs.size()==0){
			this.coefs.add(new BigFraction(0));
		}

		this.alphaMultiplier = alphaMultiplier;
	}

	public BigPolynomial(BigFraction[] coefs, BigFraction alphaMultiplier){
		this.coefs = new ArrayList();

		if(coefs.length==0){
			this.coefs.add(new BigFraction(0));
		}else {
			for (int i = 0; i < coefs.length; i++) {
				this.coefs.add(coefs[i]);
			}
		}

		this.alphaMultiplier = alphaMultiplier;
	}

	public BigPolynomial(BigPolynomial pol){
		this.coefs = new ArrayList<>();

		for(int i=0; i<pol.coefs.size(); i++){
			this.coefs.add(new BigFraction(pol.coefs.get(i)));
		}

		this.alphaMultiplier = new BigFraction(pol.alphaMultiplier);
	}

	public BigPolynomial(){
		this.coefs = new ArrayList<>();
		coefs.add(BigFraction.getZeroObject());
		this.alphaMultiplier = new BigFraction(new BigInteger(1), new BigInteger(1));
	}

	// ===========================
	// === SOME USEFUL METHODS ===
	// ===========================

    @Override
    public int compareTo(BigPolynomial arg) {
        if(this.getDeg() != arg.getDeg()){
            return this.getDeg()>arg.getDeg()?1:-1;
        }

        int diff;
        for(int i=this.getDeg(); i>=0; i--){
            if( (diff = this.getCoefOrZero(i).compareTo(arg.getCoefOrZero(i))) != 0){
                return diff;
            }
        }

        return 0;
    }

	public boolean isZero(){
		return this.coefs.size() == 1 && coefs.get(0).isZero();
	}

	private void trimLeading(){
		for(int i = this.coefs.size()-1; i>0 && this.coefs.get(i).isZero(); i--){
			this.coefs.remove(i);
		}
	}

	private void setCoef(int n, BigFraction sval){
		while(coefs.size()<=n){
			coefs.add(BigFraction.getZeroObject());
		}
		coefs.set(n, sval);
	}

	/**
	 *
	 * @param n -- index of coef to get
	 * @return copy of coef at position n, or zero, if deg of the polinom < n
	 */
	private BigFraction getCoefOrZero(int n){
		if(n < coefs.size()){
			return new BigFraction(coefs.get(n));
		}

		return BigFraction.getZeroObject();
	}

    public void toggleSign(){
        for(BigFraction coef : coefs){
            coef.toggleSign();
        }
    }

	public void toggleAlpha(){
		if(this.alphaMultiplier.isOne()){
			this.extractAlpha();
		}else{
			this.intractAlpha();
		}
	}

	public void intractAlpha(){
		for(BigFraction coef : coefs){
			coef.multiply(alphaMultiplier);
		}

		this.alphaMultiplier = new BigFraction(new BigInteger(1), new BigInteger(1));
	}

	// ====================
	// === CORE ENTRIES ===
	// ====================

	/**
	 * Supposed to solve:
	 *      P-1 aka ADD_PP_P
	 *      P-2 aka SUB_PP_P
	 */
	public void add(BigPolynomial arg){
		for(int i=0; i<arg.coefs.size(); i++){
			BigFraction coef = arg.getCoefOrZero(i);
			coef.divide(this.alphaMultiplier);
			coef.add(this.getCoefOrZero(i));
			this.setCoef(i, coef);
		}

		trimLeading();
	}

	/**
	 * Supposed to solve P-3 aka MUL_PQ_P
	 */
	private void multiply(BigFraction arg){
		for(BigFraction coef:coefs){
			coef.multiply(arg);
		}
	}

	/**
	 * Supposed to solve P-4 aka MUL_Pxk_P
	 */
	public void shiftl(int n){
		if(isZero()) return;

		for(int i=0; i<n; i++) {
			coefs.add(0, BigFraction.getZeroObject());
		}
	}

	/**
	 * Supposed to solve P-5 aka LED_P_Q
	 */
	public BigFraction leadingCoef(){
		if(isZero()){
			return BigFraction.getZeroObject();
		}

		BigFraction ret = new BigFraction(this.getCoefOrZero(this.getDeg()));
		ret.multiply(this.alphaMultiplier);

		return ret;
	}

	/**
	 * Supposed to solve P-6 aka DEG_P_N
	 */
	public int getDeg(){
		if(isZero()){
			return -1;
		}

		return this.coefs.size()-1;
	}

	/**
	 * Supposed to solve P-7 aka FAC_P_Q
	 */
	public void extractAlpha(){
		if(this.getDeg()<=0){
			return;
		}

		BigInteger gcdAll = new BigInteger();
		BigInteger lcmAll = new BigInteger(1);

		for(BigFraction coef : coefs){
			if(!coef.isZero()) {
				gcdAll = BigIntegerMath.getGCD(gcdAll, coef.getNumerator());
				lcmAll = BigIntegerMath.getLCM(lcmAll, coef.getDenominator());
			}
		}

		BigFraction newAlpha = new BigFraction(gcdAll, lcmAll);
		if(this.coefs.get(this.getDeg()).isNegative()){
			newAlpha.toggleSign();
		}

		for(BigFraction coef : coefs){
			coef.divide(newAlpha);
		}

		this.alphaMultiplier.multiply(newAlpha);
	}

	/**
	 * Supposed to solve P-8 aka MUL_PP_P
	 */
	public void multiply(BigPolynomial arg){
		if (isZero()){
			return;
		}

		if (arg.isZero()){
			this.coefs = new ArrayList<>();
			this.coefs.add(BigFraction.getZeroObject());
			return;
		}

		BigPolynomial oldThis = new BigPolynomial(this);

		for(int i=0; i<arg.coefs.size(); i++){
			BigPolynomial tmp = new BigPolynomial(oldThis);
			tmp.multiply(arg.coefs.get(i));
			tmp.shiftl(i);
			this.add(tmp);
		}

		trimLeading();
	}

	/**
	 * Supposed to solve:
     *      P-9 aka DIV_PP_P
     *      P-10 aka MOD_PP_P
	 */
	public DivModResult<BigPolynomial> divmod(BigPolynomial arg){
		if(arg.isZero()){
			throw new java.lang.IllegalArgumentException("Can't divide by zero");
		}

		BigPolynomial thisCpy = new BigPolynomial(this);
		thisCpy.intractAlpha();
		BigPolynomial argCpy = new BigPolynomial(arg);
		argCpy.intractAlpha();

		if(thisCpy.getDeg() < argCpy.getDeg()){
			return new DivModResult(new BigPolynomial(), new BigPolynomial(this));
		}

		ArrayList<BigFraction> div = new ArrayList();

		BigFraction leadingArgDigit = argCpy.leadingCoef();
		int lengthDiff = (char)(thisCpy.getDeg() - argCpy.getDeg());
		int thisLastIndex = this.getDeg();

		for(int i = 0; i <= lengthDiff; i++){

			BigFraction divDigit = new BigFraction(thisCpy.getCoefOrZero(thisLastIndex-i));
            divDigit.divide(leadingArgDigit);

			div.add(0, divDigit);

			BigPolynomial tmp = new BigPolynomial(argCpy);
			tmp.multiply(divDigit);

			tmp.shiftl(lengthDiff - i);
			tmp.toggleSign();

			thisCpy.add(tmp);
		}

		thisCpy.trimLeading();
        thisCpy.extractAlpha();

        BigPolynomial tmp = new BigPolynomial();
        tmp.coefs = div;
        tmp.extractAlpha();

		return new DivModResult(tmp, thisCpy);
	}

	/**
	 * Supposed to solve P-12 aka DER_P_P
	 */
	public void derivativate(){
		if(isZero()){
			return;
		}

		if(this.getDeg()==0){
			this.alphaMultiplier = BigFraction.getZeroObject();
			this.setCoef(0, new BigFraction(0));
		}

		BigFraction coef;
		for(int i=this.getDeg(); i>0; i--){
			coef = this.getCoefOrZero(i);
			coef.multiply(new BigFraction(i));
			this.setCoef(i, coef);
		}

		this.coefs.remove(0);

		if(this.getDeg()==0){
			intractAlpha();
		}
	}

	/**
	 * Supposed to solve P-13 aka NMR_P_P
	 */
	public void trimDegSavingRoots(){
		this.intractAlpha();

		BigPolynomial derivative = new BigPolynomial(this);
		derivative.derivativate();

		BigPolynomial gcd = BigPolynomialMath.getGCD(this, derivative);

		this.coefs = this.divmod(gcd).getDivResult().coefs;

		this.alphaMultiplier = new BigFraction(1);
		this.extractAlpha();
		this.alphaMultiplier = new BigFraction(1);
	}


    // ========================
    // === ToString methods ===
    // ========================


    @Override
    public String toString() {
		return toString(true);
    }

	public String toString(boolean inPrime) {
		if(this.getDeg() == -1){
			return "0";
		}

		StringBuilder builder = new StringBuilder();

		if(!this.alphaMultiplier.isOne()) {
			builder.append(this.alphaMultiplier.toString(inPrime));
			builder.append(" * ");
			builder.append("[ ");
		}

		boolean isFirstCoef = true;
		for(int i=this.getDeg(); i>=0; i--){
			if(!this.coefs.get(i).isZero()) {
				if(!isFirstCoef){
					if(!this.coefs.get(i).isNegative()) {
						builder.append(" + ");
					}
				}

				if(!this.coefs.get(i).isOne() || i==0) {

					String coef = this.coefs.get(i).toString(inPrime);

					if(this.coefs.get(i).isNegative()){
						builder.append(" - ");
						coef = coef.replace("-", "");
					}

					builder.append(coef);

					if (i != 0) {
						builder.append(" * ");
					}
				}

				if(i!=0){
					builder.append("x");

					if(i!=1) {
						builder.append("^");
						builder.append(i);
					}
				}

				isFirstCoef = false;
			}
		}

		if(!this.alphaMultiplier.isOne()) {
			builder.append(" ]");
		}

		return builder.toString();
	}
}
