package dev.volix.rewinside.odyssey.common.copperfield.example;

import dev.volix.rewinside.odyssey.common.copperfield.CopperConvertable;
import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField;
import dev.volix.rewinside.odyssey.common.copperfield.proto.annotation.CopperProtoClass;
import dev.volix.rewinside.odyssey.hagrid.protocol.party.PartyProtos;
import java.util.UUID;

/**
 * @author Benedikt Wüller
 */
@CopperProtoClass(type = PartyProtos.PartyMember.class)
public class PartyMember implements CopperConvertable {

    @CopperField
    public UUID uuid;

    @CopperField
    public String rank;

}