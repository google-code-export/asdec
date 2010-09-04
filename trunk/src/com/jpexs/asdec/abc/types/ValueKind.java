/*
 * Copyright (c) 2010. JPEXS
 */

package com.jpexs.asdec.abc.types;

import com.jpexs.asdec.abc.avm2.ConstantPool;


public class ValueKind {

    public static final int CONSTANT_Int = 0x03;// integer
    public static final int CONSTANT_UInt = 0x04;// uinteger
    public static final int CONSTANT_Double = 0x06;// double
    public static final int CONSTANT_Utf8 = 0x01;// string
    public static final int CONSTANT_True = 0x0B;// -
    public static final int CONSTANT_False = 0x0A;// -
    public static final int CONSTANT_Null = 0x0C;// -
    public static final int CONSTANT_Undefined = 0x00;// -
    public static final int CONSTANT_Namespace = 0x08;// namespace
    public static final int CONSTANT_PackageNamespace = 0x16;// namespace
    public static final int CONSTANT_PackageInternalNs = 0x17;// Namespace
    public static final int CONSTANT_ProtectedNamespace = 0x18;// Namespace
    public static final int CONSTANT_ExplicitNamespace = 0x19;// Namespace
    public static final int CONSTANT_StaticProtectedNs = 0x1A;// Namespace
    public static final int CONSTANT_PrivateNs = 0x05;// namespace
    private static final int optionalKinds[] = new int[]{0x03, 0x04, 0x06, 0x01, 0x0B, 0x0A, 0x0C, 0x00, 0x08, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x05};
    private static final String optionalKindNames[] = new String[]{"Int", "UInt", "Double", "Utf8", "True", "False", "Null", "Undefined", "Namespace", "PackageNamespace", "PackageInternalNs", "ProtectedNamespace", "ExplicitNamespace", "StaticProtectedNs", "PrivateNs"};
    public int value_index;
    public int value_kind;

    public ValueKind(int value_index, int value_kind) {
        this.value_index = value_index;
        this.value_kind = value_kind;
    }

    @Override
    public String toString() {
        String s = "";
        s += value_index + ":";
        boolean found = false;
        for (int i = 0; i < optionalKinds.length; i++) {
            if (optionalKinds[i] == value_kind) {
                s += optionalKindNames[i];
                found = true;
                break;
            }
        }
        if (!found) {
            s += "?";
        }
        return s;
    }

    public String toString(ConstantPool constants) {
        String ret = "?";
        switch (value_kind) {
            case CONSTANT_Int:
                ret = "" + constants.constant_int[value_index];
                break;
            case CONSTANT_UInt:
                ret = "" + constants.constant_uint[value_index];
                break;
            case CONSTANT_Double:
                ret = "" + constants.constant_double[value_index];
                break;
            case CONSTANT_Utf8:
                ret = "\"" + constants.constant_string[value_index] + "\"";
                break;
            case CONSTANT_True:
                ret = "True";
                break;
            case CONSTANT_False:
                ret = "False";
                break;
            case CONSTANT_Null:
                ret = "Null";
                break;
            case CONSTANT_Undefined:
                ret = "Undefined";
                break;
            case CONSTANT_Namespace:
                ret = "" + constants.constant_namespace[value_index].getName(constants);
                break;
            case CONSTANT_PackageInternalNs:
                ret = "" + constants.constant_namespace[value_index].getName(constants);
                break;
            case CONSTANT_ProtectedNamespace:
                ret = "protected " + constants.constant_namespace[value_index].getName(constants);
                break;
            case CONSTANT_ExplicitNamespace:
                ret = "explicit " + constants.constant_namespace[value_index].getName(constants);
                break;
            case CONSTANT_StaticProtectedNs:
                ret = "static protected " + constants.constant_namespace[value_index].getName(constants);
                break;
            case CONSTANT_PrivateNs:
                ret = "private " + constants.constant_namespace[value_index].getName(constants);
                break;
        }
        return ret;
    }
}
