package furhatos.app.mathtutor.flow

import furhatos.app.mathtutor.*
import furhatos.app.mathtutor.nlu.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import furhatos.records.Location

var waiting_time = 60000
var num_reentry = 2
//val client: SocketClient = SocketClient()

//intial state
val Start: State = state(FallbackState) {

    onEntry {
        furhat.glance(users.current)
        val location = Location(1.0, 1.0, 1.0)
        furhat.gesture(Gestures.BigSmile, async = false)

//        val user_emotion = users.current.emotion

//        print("Showing emotion: " + user_emotion)
//
//        raise(user_emotion)
        furhat.glance(location)
        furhat.ask("Hello! How can I help you? ")
    }
    onReentry {
        if (reentryCount > num_reentry) goto(MTIntro)
//        raise(users.current.emotion)
        furhat.ask("Hello! How can i help you?")
    }

    onEvent<Doubt>{
        goto(StartDoubt)
    }

    onResponse<Confused> {
        goto(MTIntro)
    }

    onResponse<Learn> {
        goto(LearnIntro)
    }
}

//when the user looks in doubt, ask if introduction is needed
val StartDoubt: State = state(FallbackState){
    onEntry {
        furhat.glance(users.current.id, duration = 1)
        furhat.say ("Hi there, you look a bit in doubt." )

        furhat.attend(users.random)
        furhat.gesture(
            Gestures.BigSmile(0.4, 2.0)
        )
        furhat.ask("Shall I introduce myself?")
    }

    onReentry { goto(Start) }

    onResponse<Yes> {
        goto(MTIntro)
    }

    onResponse<No> {
        furhat.say("That's sad.")
        goto(Start)
    }
}

//Self-introduce and ask if practice is needed
val MTIntro = state(FallbackState) {
    onEntry {
        furhat.attendAll()
        furhat.say("""I'm your personal math tutor, Mr. MT!""")

        furhat.attendNobody()
        furhat.say("""I can help you with your math problems.""".trimIndent())

        furhat.attendAll()
        furhat.ask("Would you like to learn some math now?")
    }

    onResponse<Yes> { goto(LearnIntro) }

    onResponse<No> { goto(Start) }
}

//when the user wants to learn
val LearnIntro: State = state(FallbackState) {
    onEntry {
        furhat.attendAll()
        furhat.say("""Glad that you are here!""".trimIndent())

        furhat.attendNobody()
        furhat.ask("Let's get started then! Would you like to learn about how to do " +
                "percentage problems or have some practice on it?")
    }

    onResponse<Practice> {
        goto(Practice)
    }

    onResponse<Explanations> {
        goto(Explanations)
    }
}

val Practice: State = state(FallbackState) {
    onEntry {
        furhat.attendAll()
        furhat.ask("""To adjust the difficulty, please tell me what grade you are in now.""".trimIndent())
    }

    onResponse<GradeNum> {
        var gradeNum = it.intent.gradeNum.toString().toInt()
        users.current.info.setGradeNum(gradeNum)
        if (gradeNum >= 1 && gradeNum < 3) {
            users.current.info.setLevel(PuzzleLevels.easy)
            goto(Questions)
        }
        if (gradeNum >= 3 && gradeNum < 5) {
            users.current.info.setLevel(PuzzleLevels.medium)
            goto(Questions)
        }
        if (gradeNum >= 5 && gradeNum< 7) {
            users.current.info.setLevel(PuzzleLevels.hard)
            goto(Questions)
        }
        else {
            furhat.say("Sorry, I only serve for math learning of primary level. I'm afraid I can not help you!")
        }
    }
}

val Explanations: State = state(FallbackState) {
    onEntry {
        furhat.attendAll()
        furhat.ask("Ok, let's sort this out together! I'll give a simple example here! " +
                "Suppose that we have 100 apples, then what is the percentage of one apple?")
    }
    onResponse <Confused>{
        furhat.say("Get together kid! The percentage of one apple out of 100 is 1%!")
        furhat.say("And now suppose we have N apples, " +
                "if we want to know the percentage of M apples out of N, " +
                "We can simply divide M by N, and then multiply 100 to get the percentage.")
    }
    onResponse <OnePer>{
        furhat.say("You got it! And now suppose we have N apples, " +
                "if we want to know the percentage of M apples out of N, " +
                "We can simply divide M by N, and then multiply 100 to get the percentage.")
    }
}

val Questions: State = state(FallbackState){
    onEntry {
        val level = users.current.info.getLevel()
        furhat.ask("Great! I have prepared some puzzles of ${level} level for you! Are you ready?")
    }
    onResponse<Yes> { goto(AskQuestions) }
    onResponse<No> {
        goto(WaitReady)
    }
}

val WaitReady: State = state(FallbackState){
    onEntry {
        furhat.ask("Ok! Just tell me when you are ready!", timeout = waiting_time)
    }
    onResponse<Ready> {
        goto(AskQuestions)
    }
}

val AskQuestions: State = state(FallbackState){
    onEntry {
        furhat.ask("Now, listen carefully, What's?")
    }
}
