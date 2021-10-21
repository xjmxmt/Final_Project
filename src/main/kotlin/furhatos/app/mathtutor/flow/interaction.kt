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
        furhat.ask("Let's get started then! Would you like to have some practice on ... or learn about how to do ...?")
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
        if (gradeNum >= 1 && gradeNum < 4) {
            goto(SimpleQuestions)
        }
        if (gradeNum >= 4 && gradeNum< 7) {
            goto(HardQuestions)
        }
        else {
            furhat.say("Sorry, I only serve for math learning of primary level. I'm afraid I can not help you!")
        }
    }
}

val Explanations: State = state(FallbackState) {
    onEntry {
        furhat.attendAll()
        furhat.say("You can do a percentage problem by...")
    }
}

val SimpleQuestions: State = state(FallbackState){
    onEntry {
        furhat.ask("Great! I have prepared some puzzles of percentage within ten for you! Are you ready?")
    }
    onResponse<Yes> { goto(AskQuestion) }
    onResponse<No> {
        goto(WaitReady)
    }
}

val HardQuestions: State = state(FallbackState){
    onEntry {
        furhat.ask("Great! I have prepared some puzzles of percentage within one hundred for you! Are you ready?")
    }
    onResponse<Yes> { goto(AskQuestion) }
    onResponse<No> {
        goto(WaitReady)
    }
}

val WaitReady: State = state(FallbackState){
    onEntry {
        furhat.ask("Ok! Just tell me when you are ready!", timeout = waiting_time)
    }
    onResponse<Ready> {
        goto(AskQuestion)
    }
}


// Below from Ivo:

var QuestionConfused : State = state(FallbackState){

    onEntry {
        furhat.say("Do you have ")
    }
}

/**
 * Has no GOTO yet.
 */
var AskQuestion: State = state(FallbackState){

    onEntry {
        val number_q = users.current.score.total_questions
        furhat.attendNobody()
        furhat.say("Alright, this is question number " + number_q)

        val current_q = users.current.questions.get(number_q)

        furhat.attend(users.current.id)
        furhat.ask(current_q.question)

    }

    onReentry {
        furhat.say("Do you know what the answer is?")
    }

    onResponse<Repeat> {
        val number_q = users.current.score.total_questions
        val current_q = users.current.questions.get(number_q)
        furhat.attendNobody()
        furhat.say("The question was")
        furhat.attendAll()
        furhat.ask(current_q.question)
        reentry()
    }

    onResponse<Confused> {
        furhat.say("Confused")
    }

    onResponse<QuestionAnswer>{

        val number_q = users.current.score.total_questions
        val current_q = users.current.questions.get(number_q)

        print("The returned intent value was: " + it.intent)
        print("Value should be "+ current_q.answer)
        var proposed_result = 10
        if(proposed_result == current_q.answer ){
            furhat.attendAll()
            furhat.say("YES whoo. Well done thats correct!")
            goto(AnswerCorrect)
        } else {
            furhat.attendAll()
            current_q.incrementTries()
            furhat.say("Well unfortunatly that is incorrect")
            //TODO Only go to the wrong question, if user has 2 times the question wrong. Else goto try again.
            goto(AnswerWrong)
        }

    }

}

var AnswerWrong : State = state(FallbackState){
    onEntry {
        furhat.attend(users.current.id)
        furhat.say(random(
                "OOps, that is not the correct answer.",
                "That is not the answer I was looking for",
                "Nope, thats incorrect."
        ))

        furhat.say("I will give you one more try")
    }
}

var AnswerCorrect: State = state(FallbackState){

    onEntry {
        furhat.attendNobody()
        furhat.say(random(
                "Exellent! ",
                "Good Job",
                "Perfect")
        )
        furhat.say("That is the correct answer.")


        if(users.current.score.total_questions > 5){
            goto(EnoughExercisesEndState)
        }

        furhat.attend(users.current.id)
        furhat.ask("Do you want to do another one?")

    }

    onResponse<No> {
        goto(UserWantsToStopCheck)
    }

    onResponse<Yes> {
        goto(AskQuestion)
    }

}

var Explaination : State = state(FallbackState){

}

var UserFrustrated : State = state(FallbackState){
    onEntry {
        furhat.attend(users.current.id)
        furhat.say("Hey relax, I am here to help you. Learning is a nice thing to do. ")
        furhat.attendNobody()
        furhat.say("I know it can be hard to solve these math problems")
        furhat.attend(users.current.id)
        furhat.ask("Shall I help you to explain the previous question?")
    }

    onResponse<Yes> {
        goto(ExplainAnswer)
    }

    onResponse<No> {
        goto(AskQuestion)
    }

}

var ExplainAnswer : State = state(FallbackState){
    onEntry {
        furhat.attendAll()
        furhat.say("Oke, let me explain the question.")
        furhat.attendNobody()
        val number_q = users.current.score.total_questions
        val current_q = users.current.questions.get(number_q)

        furhat.say(current_q.explaination)

        furhat.attendAll()
        furhat.ask("Do you understand it now?")
    }

    onResponse<No> {
        furhat.attendAll()
        furhat.say("I know its hard right. Let me explain it again")

        val number_q = users.current.score.total_questions
        val current_q = users.current.questions.get(number_q)

        furhat.attendNobody()
        furhat.say(current_q.explaination)


        furhat.say("Alright, lets try again");
        goto(AskQuestion)

    }

    onResponse<Yes> {
        furhat.say("Oke lets try again then!")

        //TODO Repeat the question.
        goto(AskQuestion)
    }

}

var EnoughExercisesEndState : State = state(FallbackState){
    onEntry {
        furhat.attend(users.current.id)
        furhat.say("Welldone! I think "+UserStoppedEndState+ " exercises is enough for today.")

        var wrong_q = users.current.score.num_wrong_questions
        var correct_q = users.current.score.num_correct_questions
        furhat.attendNobody()
        furhat.say("I am proud of you. You had "+correct_q+" questions correct and "+wrong_q+" questions incorrect.")
        furhat.attend(users.current.id)

        var userPoints : Number = users.current.score.points
        var score = "must "

        if(userPoints == 6 || userPoints == 7 ){
            score = "should "
        }

        if (userPoints == 8 || userPoints == 9 || userPoints == 10){
            score = "dont need "
        }

        furhat.say ("I think you "+ score + " practise more." )
        furhat.attendNobody()
        furhat.say ("Hopefully you now have enough knowledge to solve percentage questions.")
        furhat.attend(users.current.id)

    }
}

var UserWantsToStopCheck : State = state(FallbackState){
    onEntry {
        furhat.attendNobody()
        furhat.say("It will be much easier if you practise more!")
        furhat.say("We recommend to do at least 5 questions.")

        furhat.attend(users.current.id)
        furhat.ask("Do you really want to stop?")
    }

    onResponse<Yes> {
        goto(UserStoppedEndState)
    }

    onResponse<No>{
        furhat.gesture(Gestures.BigSmile, async = false)
        furhat.say("Very well! I am proud, so lets continue practising")
        goto(AskQuestion)
    }

}

var UserStoppedEndState : State = state(FallbackState){
    onEntry {
        furhat.attend(users.current.id)
        furhat.say("You can always can come back again. Gooedbye")
    }
}

// Above from Ivo:
