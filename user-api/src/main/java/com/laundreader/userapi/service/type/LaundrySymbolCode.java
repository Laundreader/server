package com.laundreader.userapi.service.type;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum LaundrySymbolCode {

    MACHINE_WASH_95("machineWash95"),
    MACHINE_WASH_70("machineWash70"),
    MACHINE_WASH_60("machineWash60"),
    MACHINE_WASH_60_MILD("machineWash60Mild"),
    MACHINE_WASH_50("machineWash50"),
    MACHINE_WASH_50_MILD("machineWash50Mild"),
    MACHINE_WASH_40("machineWash40"),
    MACHINE_WASH_40_MILD("machineWash40Mild"),
    MACHINE_WASH_40_VERY_MILD("machineWash40VeryMild"),
    MACHINE_WASH_30("machineWash30"),
    MACHINE_WASH_30_MILD("machineWash30Mild"),
    MACHINE_WASH_30_VERY_MILD("machineWash30VeryMild"),
    MACHINE_WASH_30_NEUTRAL_MILD("machineWash30NeutralMild"),
    HAND_WASH_40("handWash40"),
    HAND_WASH_40_NEUTRAL_MILD("handWash40NeutralMild"),
    HAND_WASH_30("handWash30"),
    HAND_WASH_30_NEUTRAL_MILD("handWash30NeutralMild"),
    DO_NOT_WASH("doNotWash"),

    BLEACH_CHLORINE("bleachChlorine"),
    DO_NOT_BLEACH_CHLORINE("doNotBleachChlorine"),
    BLEACH_OXYGEN("bleachOxygen"),
    DO_NOT_BLEACH_OXYGEN("doNotBleachOxygen"),
    BLEACH_ANY("bleachAny"),
    DO_NOT_BLEACH_ANY("doNotBleachAny"),

    IRON_210("iron210"),
    IRON_210_PRESSING_CLOTH("iron210PressingCloth"),
    IRON_160("iron160"),
    IRON_160_PRESSING_CLOTH("iron160PressingCloth"),
    IRON_120("iron120"),
    IRON_120_PRESSING_CLOTH("iron120PressingCloth"),
    IRON_120_NO_STEAM("iron120NoSteam"),
    DO_NOT_IRON("doNotIron"),

    DRY_CLEAN_ANY("dryCleanAny"),
    DRY_CLEAN_ANY_MILD("dryCleanAnyMild"),
    DRY_CLEAN_PETROLEUM("dryCleanPetroleum"),
    DRY_CLEAN_PETROLEUM_MILD("dryCleanPetroleumMild"),
    DRY_CLEAN_METHANE("dryCleanMethane"),
    DRY_CLEAN_METHANE_MILD("dryCleanMethaneMild"),
    DRY_CLEAN_SILICONE("dryCleanSilicone"),
    DRY_CLEAN_SILICONE_MILD("dryCleanSiliconeMild"),
    DRY_CLEAN_SPECIALIST("dryCleanSpecialist"),
    DO_NOT_DRY_CLEAN("doNotDryClean"),

    WET_CLEAN("wetClean"),
    WET_CLEAN_MILD("wetCleanMild"),
    WET_CLEAN_VERY_MILD("wetCleanVeryMild"),
    DO_NOT_WET_CLEAN("doNotWetClean"),

    WRING_MILD("wringMild"),
    DO_NOT_WRING("doNotWring"),

    LINE_DRY_SUNLIGHT("lineDrySunlight"),
    LINE_DRY_SHADE("lineDryShade"),
    LINE_DRIP_DRY_SUNLIGHT("lineDripDrySunlight"),
    LINE_DRIP_DRY_SHADE("lineDripDryShade"),
    FLAT_DRY_SUNLIGHT("flatDrySunlight"),
    FLAT_DRY_SHADE("flatDryShade"),
    FLAT_DRIP_DRY_SUNLIGHT("flatDripDrySunlight"),
    FLAT_DRIP_DRY_SHADE("flatDripDryShade"),

    TUMBLE_DRY_80("tumbleDry80"),
    TUMBLE_DRY_60("tumbleDry60"),
    DO_NOT_TUMBLE_DRY("doNotTumbleDry");

    private final String code;

    LaundrySymbolCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Set<String> getValidCodes(){
        return Arrays.stream(LaundrySymbolCode.values())
                .map(LaundrySymbolCode::getCode)
                .collect(Collectors.toSet());
    }
}
