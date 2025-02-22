package io.sc3.plethora.integration.vanilla.registry

import io.sc3.plethora.api.meta.IMetaProvider
import io.sc3.plethora.api.meta.IMetaRegistry
import io.sc3.plethora.integration.vanilla.meta.block.BlockMeta
import io.sc3.plethora.integration.vanilla.meta.block.BlockReferenceMeta
import io.sc3.plethora.integration.vanilla.meta.block.BlockStateMeta
import io.sc3.plethora.integration.vanilla.meta.blockentity.SignBlockEntityMeta
import io.sc3.plethora.integration.vanilla.meta.entity.EntityMeta
import io.sc3.plethora.integration.vanilla.meta.entity.EntityMetaProviders
import io.sc3.plethora.integration.vanilla.meta.entity.LivingEntityMeta
import io.sc3.plethora.integration.vanilla.meta.entity.PlayerEntityMeta
import io.sc3.plethora.integration.vanilla.meta.item.*
import net.minecraft.util.Identifier.DEFAULT_NAMESPACE

object VanillaMetaRegistration {
  @JvmStatic
  fun registerMetaProviders(registry: IMetaRegistry) {
    with (registry) {
      // integration.vanilla.block
      provider("block", BlockMeta())
      provider("blockReference", BlockReferenceMeta())
      provider("blockState", BlockStateMeta())

      // integration.vanilla.blockentity
      provider("signBlockEntity", SignBlockEntityMeta())

      // integration.vanilla.entity
      provider("entity", EntityMeta())
      provider("itemEntity", EntityMetaProviders.ITEM_ENTITY)
      provider("livingEntity", LivingEntityMeta())
      provider("playerEntity", PlayerEntityMeta())
      provider("sheepEntity", EntityMetaProviders.SHEEP_ENTITY)

      // integration.vanilla.item
      provider("basicItem", BasicItemMeta())
      provider("armorItem", ArmorItemMeta())
      provider("bannerItem", BannerItemMeta())
      provider("itemMaterial", ItemMaterialMeta())
      provider("foodItem", ItemMetaProviders.ITEM_FOOD)
      provider("potionItem", PotionItemMeta())
    }
  }

  private inline fun <reified T> IMetaRegistry.provider(name: String, provider: IMetaProvider<T>) {
    registerMetaProvider("$DEFAULT_NAMESPACE:$name", DEFAULT_NAMESPACE, T::class.java, provider)
  }
}
