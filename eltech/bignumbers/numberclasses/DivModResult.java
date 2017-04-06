package eltech.bignumbers.numberclasses;
/*
 Copyright (C) 2017  DoKel

 This software is free. You can use/modify/distribute it under
 terms of strong copyleft license -- GNU AGPLv3.

 You should have received a copy of the license
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

public class DivModResult<T> {
    T divResult;
    T modResult;

    DivModResult(T divResult, T modResult){
        this.divResult = divResult;
        this.modResult = modResult;
    }

    public T getModResult(){
        return modResult;
    }

    public T getDivResult(){
        return divResult;
    }
}
