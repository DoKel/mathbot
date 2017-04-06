package eltech.bignumbers.util;

/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import eltech.bignumbers.numberclasses.BigFraction;
import eltech.bignumbers.numberclasses.BigInteger;
import eltech.bignumbers.numberclasses.BigPolynomial;
import eltech.bignumbers.numberclasses.DivModResult;

import java.util.ArrayList;
import java.util.Collections;

public class BigMathParser {

	private enum MathObjectType{
		INT(1), FRAC(2), POLINOM(3), UNKNOWN(0);

		int weight;

		MathObjectType(int weight){
			this.weight = weight;
		}
	}

	private static class AnyParseResult{
		MathObjectType type;
		Object object;
	}

	public static String botname = "vika";

	public static String doCommand(String[] command){
		if(command.length<2){
			throw new java.lang.IllegalArgumentException("Command have to be built from at least two lines "+command.length);
		}

		if(command[0].startsWith(botname)){
			command[0] = command[0].replace(botname, "");
		}else{
			throw new java.lang.IllegalArgumentException("Command have to start from botname, i.e. "+botname);
		}

		StringBuilder sb = new StringBuilder();

		AnyParseResult arg1;
		AnyParseResult arg2;

		switch(command[0]){
			case ".rw":
				sb.append( "Я попыталась прочитать это...\n" );
				AnyParseResult res = tryParse(command[1]);
				switch(res.type){
					case INT:
						sb.append( "Получилось такое число:\n" );
						sb.append( ((BigInteger) res.object).toString() );
						break;
					case FRAC:
						sb.append( "Получилась такая дробь:\n" );
						sb.append( ((BigFraction) res.object).toString() );
						break;
					case POLINOM:
						sb.append("Получился такой многочлен:\n");
						sb.append(((BigPolynomial) res.object).toString());
						break;
					case UNKNOWN:
						sb.append("Но у меня не получилось :(\n");
						break;
				}
				break;
			case ".+":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command .+ needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				if(arg1.type.weight > arg2.type.weight){
					AnyParseResult tmp = arg1;
					arg1 = arg2;
					arg2 = tmp;
				}

				switch (arg1.type){
					case INT:
						switch (arg2.type){
							case INT:
								sb.append("Складываю как числа. Получилось:\n");
								BigInteger bi = (BigInteger) arg1.object;
								bi.add((BigInteger) arg2.object);
								sb.append(bi);
								break;
							case FRAC:
								sb.append("Складываю как дроби. Получилось:\n");
								BigFraction bf = new BigFraction((BigInteger) arg1.object);
								bf.add((BigFraction) arg2.object);
								sb.append(bf);
								break;
							case POLINOM:
								sb.append("Складываю как многочлены. Получилось:\n");
								BigFraction[] coefs = new BigFraction[]{
										new BigFraction(
											(BigInteger) arg1.object
										)
								};
								BigPolynomial bp = new BigPolynomial(coefs);
								bp.add((BigPolynomial) arg2.object);
								sb.append(bp);
								break;
						}
						break;
					case FRAC:
						switch (arg2.type){
							case FRAC:
								sb.append("Складываю как дроби. Получилось:\n");
								BigFraction bf = (BigFraction) arg1.object;
								bf.add((BigFraction) arg2.object);
								sb.append(bf);
								break;
							case POLINOM:
								sb.append("Складываю как многочлены. Получилось:\n");
								BigFraction[] coefs = new BigFraction[]{
										(BigFraction) arg1.object
								};
								BigPolynomial bp = new BigPolynomial(coefs);
								bp.add((BigPolynomial) arg2.object);
								sb.append(bp);
								break;
						}
						break;
					case POLINOM:
						sb.append("Складываю как многочлены. Получилось:\n");
						BigPolynomial bp = (BigPolynomial) arg1.object;
						bp.add((BigPolynomial) arg2.object);
						sb.append(bp);
						break;
				}
				break;
			case ".-":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command .- needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				switch (arg1.type){
					case INT:
						switch (arg2.type){
							case INT:
								sb.append("Вычитаю как числа. Получилось:\n");
								BigInteger bi = (BigInteger) arg1.object;
								BigInteger bi2 = (BigInteger) arg2.object;
								bi2.toggleSign();
								bi.add(bi2);
								sb.append(bi);
								break;
							case FRAC:
								sb.append("Вычитаю как дроби. Получилось:\n");
								BigFraction bf = new BigFraction((BigInteger) arg1.object);
								BigFraction bf2 = (BigFraction) arg2.object;
								bf2.toggleSign();
								bf.add(bf2);
								sb.append(bf);
								break;
							case POLINOM:
								sb.append("Вычитаю как многочлены. Получилось:\n");
								BigFraction[] coefs = new BigFraction[]{
										new BigFraction(
												(BigInteger) arg1.object
										)
								};
								BigPolynomial bp = new BigPolynomial(coefs);
								BigPolynomial bp2 = (BigPolynomial) arg2.object;
								bp2.toggleSign();
								bp.add(bp2);
								sb.append(bp);
								break;
						}
						break;
					case FRAC:
						switch (arg2.type){
							case INT:
								sb.append("Вычитаю как дроби. Получилось:\n");
								BigFraction bf = (BigFraction) arg2.object;
								BigFraction bf2 = new BigFraction((BigInteger) arg2.object);
								bf2.toggleSign();
								bf.add(bf2);
								sb.append(bf);
								break;
							case FRAC:
								sb.append("Вычитаю как дроби. Получилось:\n");
								BigFraction bf1 = (BigFraction) arg1.object;
								BigFraction bf12 = (BigFraction) arg2.object;
								bf12.toggleSign();
								bf1.add(bf12);
								sb.append(bf1);
								break;
							case POLINOM:
								sb.append("Вычитаю как многочлены. Получилось:\n");
								BigFraction[] coefs = new BigFraction[]{
										(BigFraction) arg1.object
								};
								BigPolynomial bp = new BigPolynomial(coefs);
								BigPolynomial bp2 = (BigPolynomial) arg2.object;
								bp2.toggleSign();
								bp.add(bp2);
								sb.append(bp);
								break;
						}
						break;
					case POLINOM:
						sb.append("Вычитаю как многочлены. Получилось:\n");
						BigPolynomial bp, bp2;
						switch (arg2.type){
							case INT:
								BigFraction[] coefsi = new BigFraction[]{
										new BigFraction(
												(BigInteger) arg2.object
										)
								};
								bp = (BigPolynomial) arg1.object;
								bp2 = new BigPolynomial(coefsi);
								bp2.toggleSign();
								bp.add(bp2);
								sb.append(bp);
								break;
							case FRAC:
								BigFraction[] coefsf = new BigFraction[]{
										(BigFraction) arg2.object
								};
								bp = (BigPolynomial) arg2.object;
								bp2 = new BigPolynomial(coefsf);
								bp2.toggleSign();
								bp.add(bp2);
								sb.append(bp);
								break;
							case POLINOM:
								bp = (BigPolynomial) arg1.object;
								bp2 = (BigPolynomial) arg2.object;
								bp2.toggleSign();
								bp.add(bp2);
								sb.append(bp);
								break;
						}
						break;
				}
				break;
			case ".*":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command .* needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				if(arg1.type.weight > arg2.type.weight){
					AnyParseResult tmp = arg1;
					arg1 = arg2;
					arg2 = tmp;
				}

				switch (arg1.type){
					case INT:
						switch (arg2.type){
							case INT:
								sb.append("Умножаю как числа. Получилось:\n");
								BigInteger bi = (BigInteger) arg1.object;
								bi.multiply((BigInteger) arg2.object);
								sb.append(bi);
								break;
							case FRAC:
								sb.append("Умножаю как дроби. Получилось:\n");
								BigFraction bf = new BigFraction((BigInteger) arg1.object);
								bf.multiply((BigFraction) arg2.object);
								sb.append(bf);
								break;
							case POLINOM:
								sb.append("Умножаю как многочлены. Получилось:\n");
								BigFraction[] coefs = new BigFraction[]{
										new BigFraction(
												(BigInteger) arg1.object
										)
								};
								BigPolynomial bp = new BigPolynomial(coefs);
								bp.multiply((BigPolynomial) arg2.object);
								sb.append(bp);
								break;
						}
						break;
					case FRAC:
						switch (arg2.type){
							case FRAC:
								sb.append("Умножаю как дроби. Получилось:\n");
								BigFraction bf = (BigFraction) arg1.object;
								bf.multiply((BigFraction) arg2.object);
								sb.append(bf);
								break;
							case POLINOM:
								sb.append("Умножаю как многочлены. Получилось:\n");
								BigFraction[] coefs = new BigFraction[]{
										(BigFraction) arg1.object
								};
								BigPolynomial bp = new BigPolynomial(coefs);
								bp.multiply((BigPolynomial) arg2.object);
								sb.append(bp);
								break;
						}
						break;
					case POLINOM:
						sb.append("Умножаю как многочлены. Получилось:\n");
						BigPolynomial bp = (BigPolynomial) arg1.object;
						bp.multiply((BigPolynomial) arg2.object);
						sb.append(bp);
						break;
				}
				break;
			case ".divmod":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command .divmod needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				if(arg1.type == MathObjectType.FRAC || arg2.type == MathObjectType.FRAC){
					sb.append("И как я буду делить дроби с остатком?..");
					break;
				}

				DivModResult dmres = null;

				switch(arg1.type){
					case INT:
						switch(arg2.type){
							case INT:
								sb.append("Делю с остатком как числа.\n");
								dmres = ((BigInteger) arg1.object).divmod((BigInteger) arg2.object);
								break;
							case POLINOM:
								sb.append("Делю с остатком как многочлены.\n");
								BigFraction[] coefs = new BigFraction[]{
										new BigFraction(
												(BigInteger) arg1.object
										)
								};
								BigPolynomial o1 = new BigPolynomial(coefs);
								dmres = (o1).divmod((BigPolynomial) arg2.object);
								break;
						}
						break;
					case POLINOM:
						sb.append("Делю с остатком как многочлены.\n");
						switch(arg2.type){
							case INT:
								BigFraction[] coefs = new BigFraction[]{
										new BigFraction(
												(BigInteger) arg2.object
										)
								};
								BigPolynomial o2 = new BigPolynomial(coefs);
								dmres = ((BigPolynomial) arg1.object).divmod(o2);
								break;
							case POLINOM:
								dmres = ((BigPolynomial) arg1.object).divmod((BigPolynomial) arg2.object);
								break;
						}
						break;
				}

				sb.append("Частное:\n");
				sb.append(dmres.getDivResult());
				sb.append("\nОстаток:\n");
				sb.append(dmres.getModResult());

				break;
			case "./":
			case ".\\":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command ./ needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				if(arg1.type == MathObjectType.POLINOM || arg2.type == MathObjectType.POLINOM){
					sb.append("Эта операция предназначена для деления дробей");
					break;
				}

				BigFraction f1 = null;
				BigFraction f2 = null;

				switch(arg1.type){
					case INT:
						f1 = new BigFraction((BigInteger) arg1.object);
						break;
					case FRAC:
						f1 = (BigFraction) arg1.object;
						break;
				}

				switch(arg2.type){
					case INT:
						f2 = new BigFraction((BigInteger) arg2.object);
						break;
					case FRAC:
						f2 = (BigFraction) arg2.object;
						break;
				}

				sb.append("Результат деления:\n");
				f1.divide(f2);
				sb.append(f1);

				break;
			case ".gcd":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command .gcd needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				if(arg1.type == MathObjectType.FRAC || arg2.type == MathObjectType.FRAC){
					sb.append("Все дроби делятся друг на друга, поиск НОД для них бессмысленен.");
					break;
				}


				if(arg1.type != arg2.type){
					sb.append("Нет смысла искать НОД константы и многочлена.");
					break;
				}

				if(arg1.type == MathObjectType.INT){
					sb.append("НОД этих чисел:\n");
					sb.append(BigIntegerMath.getGCD(
							(BigInteger) arg1.object,
							(BigInteger) arg2.object
					));
				}else if(arg1.type == MathObjectType.POLINOM){
					sb.append("НОД этих многочленов:\n");
					sb.append(BigPolynomialMath.getGCD(
							(BigPolynomial) arg1.object,
							(BigPolynomial) arg2.object
					));
				}

				break;
			case ".lcm":
				if(command.length<3) {
					throw new java.lang.IllegalArgumentException(
							"Command .lcm needs two arguments! ");
				}
				arg1 = tryParse(command[1]);
				arg2 = tryParse(command[2]);

				if(arg1.type == MathObjectType.UNKNOWN || arg2.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргументы");
					break;
				}

				if(arg1.type == MathObjectType.FRAC || arg2.type == MathObjectType.FRAC){
					sb.append("Все дроби делятся друг на друга, поиск НОК для них бессмысленен.");
					break;
				}

				if(arg1.type == MathObjectType.POLINOM || arg2.type == MathObjectType.POLINOM){
					sb.append("Для многочленов поиск НОК не реализован :c");
					break;
				}

				if(arg1.type == MathObjectType.INT){
					sb.append("НОК этих чисел:\n");
					sb.append(BigIntegerMath.getLCM(
							(BigInteger) arg1.object,
							(BigInteger) arg2.object
					));
				}

				break;

			case ".derivative":
			case ".der":
			case ".\'":
				arg1 = tryParse(command[1]);

				if(arg1.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргумент");
					break;
				}

				if(arg1.type != MathObjectType.POLINOM ){
					sb.append("Производная любого числа -- ноль.");
					break;
				}

				sb.append("Производная заданного многочлена:\n");
				BigPolynomial p = (BigPolynomial) arg1.object;
				p.derivativate();
				sb.append(p);

				break;
			case ".trimDegSaveRoots":
			case ".deleteDoubledRoots":
			case ".ddr":
			case ".tdsr":

				arg1 = tryParse(command[1]);

				if(arg1.type == MathObjectType.UNKNOWN){
					sb.append("Не удалось распознать аргумент");
					break;
				}

				if(arg1.type != MathObjectType.POLINOM ){
					sb.append("Какие могут быть корни у числа, милый?(");
					break;
				}

				sb.append("Я преобразовала многочлен к виду:\n");
				BigPolynomial p2 = (BigPolynomial) arg1.object;
				p2.trimDegSavingRoots();
				sb.append(p2);
				break;
		}

		return sb.toString();
	}

	public static AnyParseResult tryParse(String str){
		AnyParseResult res = new AnyParseResult();

		try{
			BigInteger bi = parseBigInteger(str);
			res.type = MathObjectType.INT;
			res.object = bi;
		}catch(Exception e1){
			try{
				BigFraction bf = parseBigFraction(str);
				res.type = MathObjectType.FRAC;
				res.object = bf;
			}catch(Exception e2){
				try {
					BigPolynomial bp = parseBigPolynomial(str);
					res.type = MathObjectType.POLINOM;
					res.object = bp;
				}catch(Exception e3){
					res.type = MathObjectType.UNKNOWN;
				}
			}
		}

		return res;
	}

	public static BigInteger parseBigInteger(String str){
		BigInteger ret;
		ArrayList<BigInteger.Digit> digits = new ArrayList();

		boolean isNegative = false;

		char[] chars = str.replaceAll("\\s", "").toCharArray();

		if(chars.length==0){
			throw new java.lang.IllegalArgumentException("Expected at least one digit, got "+str);
		}

		if(chars[0]=='-'){
			isNegative = true;
		}

		int i=isNegative?1:0;
		for(;i<chars.length; i++){
			digits.add(new BigInteger.Digit((char) Character.getNumericValue(chars[i])));
		}

		if(digits.size()==0){
			throw new java.lang.IllegalArgumentException("Expected at least one digit, got "+str);
		}

		Collections.reverse(digits);

		ret = new BigInteger(digits, false);
		if(isNegative){
			ret.toggleSign();
		}

		return ret;
	}

	public static BigFraction parseBigFraction(String str){
		str = str.replaceAll("[\\s()]", "");

		if(str.length()==0){
			throw new java.lang.IllegalArgumentException("Expected at least one digit, got: "+str);
		}

		String[] parts = str.split("[/\\\\]");

		switch(parts.length){
			case 1:
				return new BigFraction(parseBigInteger(parts[0]));
			case 2:
				return new BigFraction(parseBigInteger(parts[0]), parseBigInteger(parts[1]));
			default:
				throw new java.lang.IllegalArgumentException("Wrong input! Got: "+str);
		}
	}

	public static BigPolynomial parseBigPolynomial(String str){
		ArrayList<BigFraction> coefs = new ArrayList();

		str = str.replaceAll("[\\s]", "");

		if(str.length()==0){
			throw new java.lang.IllegalArgumentException("Expected at least one digit, got: "+str);
		}

		BigFraction alphaMult;

		String[] parts = str.split("\\*\\[");

		int i;
		switch(parts.length){
			case 1:
				alphaMult = new BigFraction(1);
				i=0;
				break;
			case 2:
				alphaMult = parseBigFraction(parts[0]);
				i=1;
				break;
			default:
				throw new java.lang.IllegalArgumentException("Wrong input! Got: "+str);
		}

		parts = parts[i]
				.replaceAll("[\\]()\\*]","")
				.replaceAll("([^+])-", "$1+-")
				.replaceAll("x(\\+|\\z)", "x^1$1")
				.replaceAll("([-\\+](\\d*)(/\\d*)?)\\z", "$1x^0")
				.split("\\+");

		for(String part: parts){
			String[] coefNpower = part.split("x\\^");
			BigFraction coef;
			if(coefNpower[0].length()==0){
				coef = new BigFraction(1);
			}else {
				coef = parseBigFraction(coefNpower[0]);
			}
			int power = Integer.parseInt(coefNpower[1]);

			while(coefs.size()<=power){
				coefs.add(new BigFraction(0));
			}

			coefs.set(power, coef);
		}


		return new BigPolynomial(coefs, alphaMult);
	}
}
