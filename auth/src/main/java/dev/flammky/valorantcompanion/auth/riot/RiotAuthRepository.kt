package dev.flammky.valorantcompanion.auth.riot

interface RiotAuthRepository {

    val activeAccount: RiotAuthenticatedAccount?
}