package fuzzlib;

import fuzzlib.norms.TNorm;

class FindMaxAandBforMembershipMIN extends FindMaxAandBforMembership {

    public FindMaxAandBforMembershipMIN() {
        super(TNorm.TN_MINIMUM);
    }
}
