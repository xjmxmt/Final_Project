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
val client: SocketClient = SocketClient()
val dialog_manager: DialogManagerClient = DialogManagerClient()

// same meaning as turn: [agent speaking, user speaking]
// remember to += 1 after each turn
var round_num = 0

// all possible user actions: 'proceed', 'silence', 'end_dialog'
var user_action = "proceed"

// has 4 levels
var user_emotion_idx = 0

// all possible agent actions: 'goto_next_state', 'smile', 'gaze', 'look_away', 'goto_encourage_state', 'say_again'
var agent_action = "smile"

//intial state
val Start: State = state(FallbackState) {

    onEntry {
        furhat.glance(users.current)

        val location = Location(1.0, 1.0, 1.0)
        furhat.gesture(Gestures.BigSmile, async = false)  // agent's initial action is smile

        val user_emotion = users.current.emotion

        print("Showing emotion: " + user_emotion)

        raise(user_emotion)
        furhat.glance(location)
        furhat.ask("Hello! How can I help you?")


    }

    onReentry {
        if (reentryCount > num_reentry) goto(MTIntro)
//        raise(users.current.emotion)
        furhat.ask("Hello! How can i help you?")
    }




    onEvent<Doubt>{

        val action = dialog_manager.callServer(round_num, user_action, user_emotion_idx, agent_action)

        if (action == "smile") {
            // smile
        } else if (action == "gaze") {
            // gaze
        } else if (action == "look away") {
            // look away
        } else if (action == "goto_encourage_state") {
            furhat.say("I know it can be hard to solve these math problems")
        } else if (action == "say_again") {
            reentry()
        }

        goto(StartDoubt)
    }

    onResponse<Confused> {

        val action = dialog_manager.callServer(round_num, user_action, user_emotion_idx, agent_action)

        if (action == "smile") {
            // smile
        } else if (action == "gaze") {
            // gaze
        } else if (action == "look away") {
            // look away
        } else if (action == "goto_encourage_state") {
            furhat.say("I know it can be hard to solve these math problems")
        } else if (action == "say_again") {
            reentry()
        }

        goto(MTIntro)
    }

    onResponse<Learn> {

        val action = dialog_manager.callServer(round_num, user_action, user_emotion_idx, agent_action)

        if (action == "smile") {
            // smile
        } else if (action == "gaze") {
            // gaze
        } else if (action == "look away") {
            // look away
        } else if (action == "goto_encourage_state") {
            furhat.say("I know it can be hard to solve these math problems")
        } else if (action == "say_again") {
            reentry()
        }

        goto(LearnIntro)
    }
}

//when the user looks in doubt, ask if introduction is needed
val StartDoubt: State = state(FallbackState){
    onEntry {
        furhat.glance(users.current.id, duration = 1)
        furhat.say ("Hi there, you look a bit in doubt.")

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

val SimpleQuestions: State = state(FallbackState){
    onEntry {
        furhat.ask("Great! I have prepared some puzzles of percentage within ten for you! Are you ready?")
    }
    onResponse<Yes> { goto(AskQuestion) }
    onResponse<No> {
        goto(WaitReady)
    }
}

val Questions: State = state(FallbackState){
    onEntry {
        val level = users.current.info.getLevel()
        furhat.ask("Great! I have prepared some puzzles of ${level} level for you! Are you ready?")
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
        furhat.attendAll()
        val number_q = users.current.score.getCurrentQuestionNumber()
        val current_q = users.current.score.getCurrentQuestion()
        val level_q = users.current.score.getCurrentLevel()

        if(number_q > 5){
            goto(EnoughExercisesEndState)
        }

        furhat.attendNobody()
        if(current_q.tries == 0){

            furhat.say("This is question number " + (number_q + 1))
            furhat.say("Lets try a " + level_q + " question. ")
        }

        if(level_q === PuzzleLevels.hard){
            furhat.say("I know its a hard question. You can do it!")
        }

        furhat.ask(current_q.question, 8000)
    }

    onReentry {
        furhat.attendAll()
        furhat.ask("What is the answer?")
    }

    onResponse<Repeat> {
        val current_q = users.current.score.getCurrentQuestion()
        furhat.attendNobody()
        furhat.say("The question was")
        furhat.attendAll()
        furhat.ask(current_q.question)
        reentry()
    }

    onResponse<Confused> {
        furhat.say("I will give you a hint")
        furhat.say(users.current.score.getCurrentQuestion().hint)
        reentry()
    }

    onResponse<No> {
        goto(ExplainAnswer)
    }

    onResponse<QuestionAnswer>{

        val current_q = users.current.score.getCurrentQuestion()

        val answer = it.intent.getAnswer().value

        if(answer == current_q.answer){
            furhat.attendAll()
            goto(AnswerCorrect)
        } else {
            furhat.attendAll()
            furhat.say("You said " + it.intent.getAnswer())
            //TODO Only go to the wrong question, if user has 2 times the question wrong. Else goto try again.
            goto(AnswerWrong)
        }
    }
}

var AnswerWrong : State = state(FallbackState){
    onEntry {
        furhat.attendAll()
        furhat.say(random(
                "That is not the answer I was looking for",
                "No, thats incorrect."
        ))

        val current_q = users.current.score.getCurrentQuestion()
        current_q.incrementTries()

        if(current_q.tries > 3){
            goto(ExplainAnswer)
        } else if (current_q.tries == 2) {
            furhat.attendAll()
            furhat.say("I can give you a hint.")
            val hint = users.current.score.getCurrentQuestion().hint
            furhat.attendNobody()
            furhat.say(hint)
            furhat.attendAll()
        }

        furhat.say(random(
                "I will give you one more try",
                "You can do it! Try again"
        )
        )
        goto(AskQuestion)
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

        val current_q = users.current.score.getCurrentQuestion()

        if(current_q.tries > 0){
            users.current.score.correctedAnswer()
        } else {
            users.current.score.correctAnswer()
        }

        furhat.say("That is the correct answer.")

        if(users.current.score.getCurrentQuestionNumber() >= 5){
            goto(EnoughExercisesEndState)
        }



        furhat.attendAll()

         val random = Math.random() * 2

        if(random > 1){
            furhat.ask("Do you want to do another one?")
        } else {
            furhat.say("Lets continue to the next question.")
        }
        goto(AskQuestion)

    }

    onReentry {
        furhat.attendNobody()
        furhat.say("I recommend to do at least five in total")
        furhat.attendAll()
        furhat.ask("Do you want to do another question? ")
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
        furhat.attendAll()
        furhat.say("Hey relax, I am here to help you. Learning is a nice thing to do. ")
        furhat.attendNobody()
        furhat.say("I know it can be hard to solve these math problems")
        furhat.attendAll()
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
        furhat.say("Let me explain the question.")

        val current_q = users.current.score.getCurrentQuestion()
        furhat.attendNobody()
        furhat.say(current_q.explaination)

        furhat.attendAll()
        furhat.ask("Do you understand it now?")
    }

    onResponse<No> {
        furhat.attendAll()
        furhat.say("I know its hard right. Let me explain it again")

        val current_q = users.current.score.getCurrentQuestion()

        furhat.attendNobody()
        furhat.say(current_q.explaination)


        furhat.say("Alright, lets try the next question");
        users.current.score.incorrectAnswer()
        goto(AskQuestion)

    }

    onResponse<Yes> {
        furhat.say("Oke lets try again with the next question!")

        //TODO Repeat the question.
        users.current.score.incorrectAnswer()
        goto(AskQuestion)
    }

}

var EnoughExercisesEndState : State = state(FallbackState){
    onEntry {
        furhat.attendAll()
        furhat.say("Welldone! I think "+ users.current.score.question_history.size + " exercises is enough for today.")

        val wrong_q = users.current.score.num_wrong_questions
        val correct_q = users.current.score.num_correct_questions
        furhat.attendNobody()
        furhat.say("I am proud of you. You had "+correct_q+" questions correct and "+wrong_q+" questions incorrect.")
        furhat.attendAll()

        val userPoints : Double = users.current.score.getScore()
        var score = "must "

        if(userPoints > 1.0 ){
            score = "should "
        }

        if (userPoints >= 1.0){
            score = "dont need "
        }

        furhat.say ("I think you "+ score + " practise more." )
        furhat.attendNobody()
        furhat.say ("Hopefully you now have enough knowledge to solve percentage questions.")
        furhat.attendAll()
        furhat.say("You can always come back and train more if you like to! Goodbye")

    }
}

var UserWantsToStopCheck : State = state(FallbackState){
    onEntry {
        furhat.attendNobody()
        furhat.say("It will be much easier if you practise more!")
        furhat.say("We recommend to do at least 5 questions.")

        furhat.attendAll()
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
        furhat.attendAll()
        furhat.say("You can always can come back again. Goodbye")
    }
}

// Above from Ivo:
