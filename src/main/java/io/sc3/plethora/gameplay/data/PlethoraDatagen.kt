package io.sc3.plethora.gameplay.data

import io.sc3.plethora.gameplay.data.recipes.RecipeGenerator
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory

object PlethoraDatagen : DataGeneratorEntrypoint {
  val log = LoggerFactory.getLogger("Plethora/PlethoraDatagen")!!

  override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
    log.info("Plethora datagen initializing")

    val pack = generator.createPack()
    val turtleUpgrades = pack.addProvider(::TurtleUpgradeProvider)
    val pocketUpgrades = pack.addProvider(::PocketUpgradeProvider)
    pack.addProvider(::ModelProvider)
    pack.addProvider(::BlockLootTableProvider)
    pack.addProvider(::BlockTagProvider)
    pack.addProvider { out, _ -> RecipeGenerator(out, turtleUpgrades, pocketUpgrades) }
  }
}
