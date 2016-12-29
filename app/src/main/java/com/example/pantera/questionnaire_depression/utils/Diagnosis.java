package com.example.pantera.questionnaire_depression.utils;

import com.example.pantera.questionnaire_depression.R;

/**
 * Created by Pantera on 2016-12-28.
 */

public enum Diagnosis {
    BEZ_DEPRESJI(new MyRange<>(0, 11)), ŁAGODNA_DEPRESJA(new MyRange<>(12, 26)), UMIARKOWANIE_CIĘŻKA_DEPRESJA(new MyRange<>(27, 49)), BARDZO_CIĘŻKA_DEPRESJA(new MyRange<>(50, 63));

    private final MyRange<Integer> points;

    Diagnosis(MyRange<Integer> points) {
        this.points = points;
    }

    public static String getDiagnose(int points) {
        Diagnosis found = BEZ_DEPRESJI;
        for (Diagnosis d : values()) {
            if (points >= d.points.getLower() && points <=d.points.getUpper()) {
                found = d;
            }
        }
        return found.toString().replace("_", " ").toLowerCase();
    }

    public static int getColor(int points){
        Diagnosis found = BEZ_DEPRESJI;
        for (Diagnosis d : values()) {
            if (points >= d.points.getLower() && points <=d.points.getUpper()) {
                found = d;
            }
        }
        int color;
        switch (found){
            case BEZ_DEPRESJI:
                color = R.color.color1;
                break;
            case  ŁAGODNA_DEPRESJA:
                color = R.color.color2;
                break;
            case UMIARKOWANIE_CIĘŻKA_DEPRESJA:
                color = R.color.color3;
                break;
            case BARDZO_CIĘŻKA_DEPRESJA:
                color = R.color.color4;
                break;
            default:
                color = R.color.input_register_bg;
                break;
        }
        return color;
    }

    public static class MyRange<T> {
        private T lower;
        private T upper;

        MyRange(T lower, T upper) {
            this.lower = lower;
            this.upper = upper;
        }

        T getLower() {
            return lower;
        }

        public MyRange setLower(T lower) {
            this.lower = lower;
            return this;
        }

        T getUpper() {
            return upper;
        }

        public MyRange setUpper(T upper) {
            this.upper = upper;
            return this;
        }
    }
}
