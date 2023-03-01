package commands

import com.github.twitch4j.TwitchClient
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import com.github.twitch4j.helix.domain.BanUserInput
import dao.dao
import kotlinx.coroutines.runBlocking
import models.*
import settings.auth
import settings.maxChance
import settings.trustableUser

val etcIndex=listOf(
    Command("룰렛", ::roulette, 0, false),
    Command("rouletteMod", ::modifyAsSudoers, 2, true)
)

fun ban(client: TwitchClient, event: ChannelMessageEvent,userID:String,duration:Int,reason:String="") {
    client.helix.banUser(auth.token, event.channel.id, auth.userID, BanUserInput(userID, reason, duration)).execute()
}

fun roulette(client: TwitchClient, event: ChannelMessageEvent, ignoredArgs: List<String>): String {
    if (
        runBlocking {
            val user = dao.existUser(event.user.id)
            val roulette = user.let { it.listenerData.roulette[event.channel.id.toInt()] ?: Roulette() }
            if (roulette.lastEditedTime < System.currentTimeMillis() - (86_400_000L)) /*lastEditedTime < now -1day*/ {
                dao.editUser(
                    event.user.id,
                    user.streamerData,
                    user.listenerData.editRoulette(event.channel.id, chances = maxChance.data) //recover day by day
                )
                return@runBlocking false
            }
        return@runBlocking roulette.chances<=0
    }) return "오늘 룰렛을 돌리기에는 머리가 너무 많이 깨졌습니다."
    
    return if ((1..6).random() == 6) {
        val score: Int
        runBlocking {
            val user= dao.existUser(event.user.id)
            score = user.listenerData.roulette[event.channel.id.toInt()]?.combo ?: 0 // 0 if record not exist
            
            //writing db
            dao.editUser(
                event.user.id,
                user.streamerData, //pass it without modifying
                user.listenerData.editRoulette(event.channel.id, (-1).offset, 0.data)
            )
        }
            ban(client, event, event.user.id, 10, "러시안 룰렛당해버린") //timeout 10s
        "${event.user.name} -> 탕! ${score}번 살아남으셨습니다!"
    }
    else{
        val score:Int
        runBlocking {
            val user= dao.existUser(event.user.id)
            score = (user.listenerData.roulette[event.channel.id.toInt()]?.combo ?: 0) + 1
            dao.editUser(
                event.user.id,
                user.streamerData,
                user.listenerData.editRoulette(event.channel.id, 0.offset, 1.offset)
            ) // if record exist, combo++
        }
        "${event.user.name} -> 찰캌! ${score}번 살아남으셨습니다!"
    }
}

fun modifyAsSudoers(client: TwitchClient, event: ChannelMessageEvent, args: List<String>): String? {
    if (event.user.name !in trustableUser)
        return null
    
    return runBlocking {
        val uid =
            args[0].toIntOrNull() ?: client.helix.getUsers(null, null, listOf(args[0])).execute().users[0].id.toInt()
        
        val user = dao.existUser(uid)
        
        val channelId = args.getOrNull(2)?.toInt() ?: event.channel.id.toInt()
        
        //writing db
        dao.editUser(
            uid,
            user.streamerData, //pass it without modifying
            user.listenerData.editRoulette(channelId, 3.data, args[1].toInt().data)
        )
        return@runBlocking user.listenerData.roulette[channelId]?.combo?.toString() ?: "0"
    }
}
