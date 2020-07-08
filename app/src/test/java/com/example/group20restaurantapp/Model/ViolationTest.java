package com.example.group20restaurantapp.Model;

import com.example.group20restaurantapp.R;

import org.junit.Test;

import static org.junit.Assert.*;

public class ViolationTest {

    @Test
    public void testViolImgId1() {
        // Test standalone violation
        Violation viol = new Violation(101,
                false,
                "???",
                "???",
                false);
        assertEquals(R.drawable.violation_construction, viol.getViolImgId());
    }

    @Test
    public void testViolImgId2() {
        // Test or statement
        Violation viol = new Violation(305,
                false,
                "???",
                "???",
                false);
        assertEquals(R.drawable.violation_rat, viol.getViolImgId());
    }

    @Test
    public void testViolImgId3() {
        // Test no matching violation number
        Violation viol = new Violation(99,
                false,
                "???",
                "???",
                false);
        assertEquals(R.drawable.violation_generic, viol.getViolImgId());
    }

    @Test
    public void testViolImgId4() {
        // Test permitArrayList
        Violation viol = new Violation(314,
                false,
                "???",
                "???",
                false);
        assertEquals(R.drawable.violation_permit, viol.getViolImgId());
    }

    @Test
    public void testViolImgId5() {
        // Test badFoodArrayList
        Violation viol = new Violation(206,
                false,
                "???",
                "???",
                false);
        assertEquals(R.drawable.violation_bad_food, viol.getViolImgId());
    }

    @Test
    public void testViolImgId6() {
        // Test utensilsArrayList
        Violation viol = new Violation(308,
                false,
                "???",
                "???",
                false);
        assertEquals(R.drawable.violation_utensils, viol.getViolImgId());
    }

}