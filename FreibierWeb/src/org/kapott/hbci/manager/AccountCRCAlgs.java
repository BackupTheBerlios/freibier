
/*  $Id: AccountCRCAlgs.java,v 1.1 2005/04/05 21:34:47 tbayen Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2004  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.manager;

public class AccountCRCAlgs
{
    public static boolean alg_00(int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2}, true);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }

    public static boolean alg_01(int[] number) 
    {
        int sum = addProducts(number, 0, 8,
                              new int[] { 1, 7, 3, 1, 7, 3, 1, 7, 3 }, false);
        int crc = (10 - sum % 10) % 10;
        return number[9] == crc;
    }

    public static boolean alg_02(int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {2,9,8,7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_03(int[] number) 
    {
        int sum=addProducts(number,0,8,new int[] {2,1,2,1,2,1,2,1,2},false);
        int crc=(10-sum%10)%10;
        return number[9]==crc;
    }

    public static boolean alg_04(int[] number) 
    {
        int sum = addProducts(number,0,8,new int[] {4,3,2,7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_06(int[] number) 
    {
        int sum = addProducts(number, 0, 8,
                              new int[] { 4, 3, 2, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_09(int[] number) 
    {
        return true;
    }

    public static boolean alg_10(int[] number) 
    {
        int sum = addProducts(number, 0, 8,
                              new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_13(int[] number) 
    {
        int sum
            = addProducts(number, 1, 6, new int[] { 1, 2, 1, 2, 1, 2 }, true);
        int crc = (10 - sum % 10) % 10;
        
        boolean ok=(number[7]==crc);
        if (!ok) {
            sum=addProducts(number,3,8,new int[] { 1, 2, 1, 2, 1, 2 }, true);
            crc = (10 - sum % 10) % 10;
            ok=(number[9]==crc);
        }
        return ok;    
    }

    public static boolean alg_14(int[] number) 
    {
        int sum = addProducts(number,3,8,new int[] {7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_19(int[] number) 
    {
        int sum = addProducts(number, 0, 8,
                              new int[] { 1, 9, 8, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_20(int[] number) 
    {
        int sum = addProducts(number, 0, 8,
                              new int[] { 3, 9, 8, 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_28(int[] number) 
    {
        int sum = addProducts(number, 0, 6, new int[] { 8, 7, 6, 5, 4, 3, 2 },
                              false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[7] == crc;
    }

    public static boolean alg_32(int[] number) 
    {
        int sum
            = addProducts(number, 3, 8, new int[] { 7, 6, 5, 4, 3, 2 }, false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_33(int[] number) 
    {
        int sum = addProducts(number,4,8,new int[] {6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
        return number[9]==crc;
    }

    public static boolean alg_34(int[] number) 
    {
        int sum = addProducts(number, 0, 6, new int[] { 7, 9, 10, 5, 8, 4, 2 },
                              false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[7] == crc;
    }

    public static boolean alg_38(int[] number) 
    {
        int sum = addProducts(number, 3, 8, new int[] { 9, 10, 5, 8, 4, 2 },
                              false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    /* *** public static boolean alg_52(int[] number) 
    {
    }*/

    public static boolean alg_61(int[] number) 
    {
        int crc;

        if (number[8]==8) {
            int sum=addProducts(number,0,9,new int[] {2,1,2,1,2,1,2,0,1,2},true);
            crc=(10-sum%10)%10;
        } else {
            int sum=addProducts(number,0,6,new int[] {2,1,2,1,2,1,2},true);
            crc=(10-sum%10)%10;
        }
        return number[7]==crc;
    }

    public static boolean alg_63(int[] number) 
    {
        boolean ok=false;
        
        if (number[0] != 0) {
            ok=false;
        } else if (number[1]==0 && number[2]==0) {
            int sum = addProducts(number, 3, 8, new int[] { 1, 2, 1, 2, 1, 2 }, true);
            int crc = (10 - sum % 10) % 10;
            ok=(number[9] == crc);
        } else {
            int sum = addProducts(number, 1, 6, new int[] { 1, 2, 1, 2, 1, 2 }, true);
            int crc = (10 - sum % 10) % 10;
            ok=(number[7] == crc);
        }
        
        return ok;
    }

    public static boolean alg_65(int[] number) 
    {
        int crc;
        
        if (number[8]==9) {
            int sum=addProducts(number,0,9,new int[] {2,1,2,1,2,1,2,0,1,2},true);
            crc=(10-sum%10)%10;
        } else {
            int sum=addProducts(number,0,6,new int[] {2,1,2,1,2,1,2},true);
            crc=(10-sum%10)%10;
        }
        return number[7]==crc;
    }

    /* ***
    public static boolean alg_68(int[] number) 
    {
    }*/

    public static boolean alg_70(int[] number) 
    {
        int crc;
        
        if (number[3]==5 || (number[3]==6 && number[4]==9)) {
            int sum = addProducts(number,3,8,new int[] {7,6,5,4,3,2},false);
            crc=11-(sum%11);
            if (crc>9)
                crc=0;
        } else {
            int sum = addProducts(number,0,8,new int[] {4,3,2,7,6,5,4,3,2},false);
            crc=11-(sum%11);
            if (crc>9)
                crc=0;
        }
        return number[9]==crc;
    }

    public static boolean alg_76(int[] number) 
    {
        int sum
            = addProducts(number, 1, 6, new int[] { 7, 6, 5, 4, 3, 2 }, false);
        int crc = sum % 11;
        return number[7] == crc;
    }

    public static boolean alg_88(int[] number) 
    {
        int sum = 0;
        if (number[2] == 9)
            sum = addProducts(number, 2, 8, new int[] { 8, 7, 6, 5, 4, 3, 2 },
                              false);
        else
            sum = addProducts(number, 3, 8, new int[] { 7, 6, 5, 4, 3, 2 },
                              false);
        int crc = 11 - sum % 11;
        if (crc > 9)
            crc = 0;
        return number[9] == crc;
    }

    public static boolean alg_91(int[] number) 
    {
        int sum = addProducts(number,0,5,new int[] {7,6,5,4,3,2},false);
        int crc=11-(sum%11);
        if (crc>9)
            crc=0;
            
        if (number[6]!=crc) {
            sum = addProducts(number,0,5,new int[] {2,3,4,5,6,7},false);
            crc=11-(sum%11);
            if (crc>9)
                crc=0;
                
            if (number[6]!=crc) {
                sum = addProducts(number,0,9,new int[] {10,9,8,7,6,5,0,4,3,2},false);
                crc=11-(sum%11);
                if (crc>9)
                    crc=0;
            }
        }
        
        return number[6]==crc;
    }

    public static boolean alg_96(int[] number) 
    {
        boolean ret = false;
        if (!(ret = alg_19(number)) && !(ret = alg_00(number))) {
            int bigint = 0;
            if (number[0] == 0)
                bigint = calculateIntFromNumber(number);
            if (bigint >= 1300000 && bigint <= 99399999)
                ret = true;
        }
        return ret;
    }

    public static boolean alg_99(int[] number) 
    {
        int bigint = 0;
        if (number[0] == 0)
            bigint = calculateIntFromNumber(number);
        boolean ret = true;
        if (bigint < 396000000 || bigint > 499999999) {
            int sum
                = addProducts(number, 0, 8,
                              new int[] { 4, 3, 2, 7, 6, 5, 4, 3, 2 }, false);
            int crc = 11 - sum % 11;
            if (crc > 9)
                crc = 0;
            ret = number[9] == crc;
        }
        return ret;
    }

    private static int addProducts(int[] number, int first, int last,
                                   int[] factors, boolean withChecksum) 
    {
        int result = 0;
        for (int i = first; i <= last; i++) {
            int prod = number[i] * factors[i - first];
            if (withChecksum)
                prod = prod / 10 + prod % 10;
            result += prod;
        }
        return result;
    }

    private static int calculateIntFromNumber(int[] number) 
    {
        int bigint = 0;
        for (int i = 1; i < 10; i++) {
            bigint *= 10;
            bigint += number[i];
        }
        return bigint;
    }
}
