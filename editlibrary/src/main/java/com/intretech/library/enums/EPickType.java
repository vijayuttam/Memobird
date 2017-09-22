package com.intretech.library.enums;

import java.util.Arrays;

public enum EPickType {
    CAMERA, GALLERY;

    public boolean inside(EPickType[] array) {
        return Arrays.asList(array).contains(this);
    }

    public static EPickType[] fromInt(int val) {
        if (val > values().length - 1){
            return new EPickType[] {CAMERA, GALLERY};
        }else{
            return new EPickType[] {values()[val]};
        }
    }
}
