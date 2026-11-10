package model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CategoryTest {

    @Test
    public void upgradeMovesOneStepTowardCat1() {
        assertEquals(Category.CAT_4, Category.CAT_5.upgrade());
        assertEquals(Category.CAT_3, Category.CAT_4.upgrade());
        assertEquals(Category.CAT_2, Category.CAT_3.upgrade());
        assertEquals(Category.CAT_1, Category.CAT_2.upgrade());
    }

    @Test
    public void upgradeFromCat1StaysAtCat1() {
        assertEquals(Category.CAT_1, Category.CAT_1.upgrade());
    }
}
