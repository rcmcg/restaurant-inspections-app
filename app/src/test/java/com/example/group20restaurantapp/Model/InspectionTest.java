package com.example.group20restaurantapp.Model;

import com.example.group20restaurantapp.R;

import org.junit.Test;

import static org.junit.Assert.*;

public class InspectionTest {

    @Test
    public void testViolImgId1() {
        // Test standalone violation
        Inspection.Violation viol = new Inspection.Violation("Follow-up",
                "???",
                101);
        assertEquals(R.drawable.violation_construction, viol.getViolImgId());
    }

    @Test
    public void testViolImgId2() {
        // Test or statement
        Inspection.Violation viol = new Inspection.Violation("Follow-up",
                "???",
                305);
        assertEquals(R.drawable.violation_rat, viol.getViolImgId());
    }

    @Test
    public void testViolImgId3() {
        // Test no matching violation number
        Inspection.Violation viol = new Inspection.Violation("Follow-up",
                "???",
                99);
        assertEquals(R.drawable.violation_generic, viol.getViolImgId());
    }

    @Test
    public void testViolImgId4() {
        // Test permitArrayList
        Inspection.Violation viol = new Inspection.Violation("Follow-up",
                "???",
                314);
        assertEquals(R.drawable.violation_permit, viol.getViolImgId());
    }

    @Test
    public void testViolImgId5() {
        // Test badFoodArrayList
        Inspection.Violation viol = new Inspection.Violation("Follow-up",
                "???",
                206);
        assertEquals(R.drawable.violation_bad_food, viol.getViolImgId());
    }

    @Test
    public void testViolImgId6() {
        // Test utensilsArrayList
        Inspection.Violation viol = new Inspection.Violation("Follow-up",
                "???",
                308);
        assertEquals(R.drawable.violation_utensils, viol.getViolImgId());
    }

}