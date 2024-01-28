package io.sc3.plethora.gameplay.modules.sensor;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import io.sc3.plethora.Plethora;
import io.sc3.plethora.api.IWorldLocation;
import io.sc3.plethora.api.method.FutureMethodResult;
import io.sc3.plethora.api.method.IContext;
import io.sc3.plethora.api.method.IUnbakedContext;
import io.sc3.plethora.api.module.IModuleContainer;
import io.sc3.plethora.api.module.SubtargetedModuleMethod;
import io.sc3.plethora.api.reference.Reference;
import io.sc3.plethora.gameplay.modules.RangeInfo;
import io.sc3.plethora.integration.vanilla.meta.entity.EntityMeta;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static io.sc3.plethora.api.method.ContextKeys.ORIGIN;
import static io.sc3.plethora.core.ContextHelpers.fromContext;
import static io.sc3.plethora.gameplay.modules.sensor.SensorHelpers.*;
import static io.sc3.plethora.gameplay.registry.PlethoraModules.SENSOR_M;

public class SensorMethods {
    public static final SubtargetedModuleMethod<IWorldLocation> SENSE = SubtargetedModuleMethod.of(
        "sense", SENSOR_M, IWorldLocation.class,
        "function():table -- Scan for entities in the vicinity",
        SensorMethods::sense
    );
    private static FutureMethodResult sense(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                            @Nonnull IArguments args) throws LuaException {
        SensorMethodContext ctx = getContext(unbaked);
        World world = ctx.loc.getWorld();
        BlockPos pos = ctx.loc.getPos();

        return ctx.context.getCostHandler().await(ctx.range.getBulkCost(), () -> {
            List<Entity> entities = world.getEntitiesByClass(Entity.class,
                getBox(pos, ctx.range.getRange()), DEFAULT_PREDICATE);

            // TODO: Helpers.map?
            return FutureMethodResult.result(entities.stream()
                .map(x -> EntityMeta.getBasicProperties(x, ctx.loc))
                .toList());
        });
    }

    public static final SubtargetedModuleMethod<IWorldLocation> GET_META_BY_ID = SubtargetedModuleMethod.of(
        "getMetaByID", SENSOR_M, IWorldLocation.class,
        "function(id:string):table|nil -- Find a nearby entity by UUID",
        SensorMethods::getMetaById
    );
    private static FutureMethodResult getMetaById(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                  @Nonnull IArguments args) throws LuaException {
        SensorMethodContext ctx = getContext(unbaked);
        int radius = ctx.range.getRange();

        UUID uuid;
        try {
            uuid = UUID.fromString(args.getString(0));
        } catch (IllegalArgumentException e) {
            throw new LuaException("Invalid UUID");
        }

        Entity entity = findEntityByUuid(ctx.loc, radius, uuid);
        if (entity == null) return null;

        return FutureMethodResult.result(ctx.context.makeChild(entity, Reference.bounded(entity, ctx.loc, radius))
            .getMeta());
    }

    public static final SubtargetedModuleMethod<IWorldLocation> GET_META_BY_NAME = SubtargetedModuleMethod.of(
        "getMetaByName", SENSOR_M, IWorldLocation.class,
        "function(name:string):table|nil -- Find a nearby entity by name",
        SensorMethods::getMetaByName
    );
    private static FutureMethodResult getMetaByName(@Nonnull IUnbakedContext<IModuleContainer> unbaked,
                                                    @Nonnull IArguments args) throws LuaException {
        try {
            SensorMethodContext ctx = getContext(unbaked);
            int radius = ctx.range.getRange();

            Entity entity = findEntityByName(ctx.loc, radius, args.getString(0));
            if (entity == null) return FutureMethodResult.empty();

            return FutureMethodResult.result(ctx.context.makeChild(entity,
              Reference.bounded(entity, ctx.loc, radius)).getMeta());
        } catch (Exception e) {
            Plethora.log.error("Error in getMetaByName", e);
            throw new LuaException("Unknown error in getMetaByName");
        }
    }

    private record SensorMethodContext(IContext<IModuleContainer> context, IWorldLocation loc, RangeInfo range) {}
    private static SensorMethodContext getContext(@Nonnull IUnbakedContext<IModuleContainer> unbaked) throws LuaException {
        IContext<IModuleContainer> ctx = unbaked.bake();
        IWorldLocation location = fromContext(ctx, IWorldLocation.class, ORIGIN);
        RangeInfo range = fromContext(ctx, RangeInfo.class, SENSOR_M);
        return new SensorMethodContext(ctx, location, range);
    }
}
