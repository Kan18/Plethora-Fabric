package io.sc3.plethora.integration.vanilla.meta.item

import io.sc3.plethora.api.meta.ItemStackMetaProvider
import net.minecraft.item.*
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtString

object WritableBookItemMeta : ItemStackMetaProvider<WritableBookItem>(WritableBookItem::class.java) {
    override fun getMeta(stack: ItemStack, item: WritableBookItem): Map<String, *> = mapOf(
        "pages" to stack.nbt?.getList("pages", NbtCompound.STRING_TYPE.toInt())?.toArray()
            ?.filterIsInstance<NbtString>()
            ?.map { it.asString() }
    )
}
