package dev.flammky.valorantcompanion.pvp.store

sealed class ItemType(
    val name: String,
    val id: String
) {

    object Agent : ItemType(
        name = "agent",
        id = "01bb38e1-da47-4e6a-9b3d-945fe4655707"
    )

    object Contract : ItemType(
        name = "contract",
        id = "f85cb6f7-33e5-4dc8-b609-ec7212301948"
    )

    object Spray : ItemType(
        name = "spray",
        id = "d5f120f8-ff8c-4aac-92ea-f2b5acbe9475"
    )

    object GunBuddy : ItemType(
        name = "gun buddy",
        id = "dd3bf334-87f3-40bd-b043-682a57a8dc3a"
    )

    object Card : ItemType(
        name = "card",
        id = "3f296c07-64c3-494c-923b-fe692a4fa1bd"
    )

    object Skin : ItemType(
        name = "skin",
        id = "e7c63390-eda7-46e0-bb7a-a6abdacd2433"
    )

    object SkinVariant : ItemType(
        name = "skin variant",
        id = "3ad1b2b2-acdb-4524-852f-954a76ddae0a"
    )

    object Title : ItemType(
        name = "title",
        id = "de7caa6b-adf7-4588-bbd1-143831e786c6"
    )
}