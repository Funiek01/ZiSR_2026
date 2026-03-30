package fuzzlib;

import fuzzlib.norms.SNorm;

class FindMaxAandBforMembershipMAX extends FindMaxAandBforMembership {

    public FindMaxAandBforMembershipMAX() {
        super(SNorm.SN_MAXIMUM);
    }
}
