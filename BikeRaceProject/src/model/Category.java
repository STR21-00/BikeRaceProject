package model;

// <<enumeration>> Category, from the DP diagram. 
/*  Notes for me: 
CAT_5 is entry level, CAT_1 is the top. 
Notes for me: a special java type for a fixed representation (constant values)/ closed list defined once.
*/

public enum Category {
    CAT_5, CAT_4, CAT_3, CAT_2, CAT_1;

    /// One step toward CAT_1. Already at CAT_1 -> stays at CAT_1. 
    
    public Category upgrade() {
        switch (this) {
            case CAT_5: 
                return CAT_4;
            case CAT_4: 
                return CAT_3;
            case CAT_3: 
                return CAT_2;
            case CAT_2:
                return CAT_1;

            default: return CAT_1;
        }
    }
}
