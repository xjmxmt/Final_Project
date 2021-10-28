package furhatos.app.mathtutor
import furhatos.app.mathtutor.flow.*
import furhatos.event.Event
import furhatos.flow.kotlin.furhat
import furhatos.gestures.Gesture
import furhatos.gestures.Gestures
import furhatos.records.User
import java.lang.Exception
import java.net.Socket
import java.util.*

val User.info : CurrentUser
    get() = data.getOrPut(CurrentUser::class.qualifiedName, CurrentUser())

val User.affect : Int
    get () = Affect().getAffect()

public class Affect {
    val client = SocketClient()

    public fun getAffect() : Int {
        var emotion  = ""
        try{
            emotion = client.callServer()
        }catch (e: RuntimeException){
            print("Could not fetch affect. Is the server online? ")
            println(e);
        }

        var affection = AffectEnumAll.Nothing
        try{
            affection = AffectEnumAll.valueOf(emotion);
        }catch (e : Exception){
            print("Could not find return value inside the defined Affect enum. ")
        }

        // 4 levels of emotion
        // 0: positive, 1: not very frustration, 2: a bit frustration, 3: frustration
        return when(affection){
            AffectEnumAll.Affection -> 0
            AffectEnumAll.Anticipation -> 0
            AffectEnumAll.Confidence -> 0
            AffectEnumAll.Engagement -> 0
            AffectEnumAll.Esteem -> 0
            AffectEnumAll.Excitement -> 0
            AffectEnumAll.Happiness -> 0
            AffectEnumAll.Peace -> 0
            AffectEnumAll.Pleasure -> 0
            AffectEnumAll.Sensitivity -> 0
            AffectEnumAll.Surprise -> 0
            AffectEnumAll.Sympathy -> 0
            AffectEnumAll.Yearning ->0
            AffectEnumAll.Nothing -> 0

            AffectEnumAll.Disconnection -> 1
            AffectEnumAll.Disquietment -> 1
            AffectEnumAll.Doubt -> 1
            AffectEnumAll.Confusion -> 1
            AffectEnumAll.Embarrassment -> 1

            AffectEnumAll.Annoyance -> 2
            AffectEnumAll.Disapproval -> 2
            AffectEnumAll.Fatigue -> 2
            AffectEnumAll.Fear -> 2

            AffectEnumAll.Anger -> 3
            AffectEnumAll.Aversion -> 3
            AffectEnumAll.Pain -> 3
            AffectEnumAll.Sadness -> 3
            AffectEnumAll.Suffering -> 3
        }
    }

    // Use for raising user emotion as event
//    public fun getAffect() : Event {
//        var emotion  = ""
//        try{
//            emotion = client.callServer()
//        }catch (e: RuntimeException){
//            print("Could not fetch affect. Is the server online? ")
//            println(e);
//        }
//
//        var affection = AffectEnumAll.Nothing
//        try{
//            affection = AffectEnumAll.valueOf(emotion);
//        }catch (e : Exception){
//            print("Could not find return value inside the defined Affect enum. ")
//        }
//
//        return when(affection){
//            AffectEnumAll.Affection -> Annoyed()
//            AffectEnumAll.Anger -> Annoyed()
//            AffectEnumAll.Annoyance -> Annoyed()
//            AffectEnumAll.Aversion -> Annoyed()
//            AffectEnumAll.Disquietment -> Annoyed()
//            AffectEnumAll.Fatigue -> Annoyed()
//            AffectEnumAll.Pain -> Annoyed()
//            AffectEnumAll.Sadness -> Annoyed()
//            AffectEnumAll.Sensitivity -> Annoyed()
//            AffectEnumAll.Suffering -> Annoyed()
//
//            AffectEnumAll.Disapproval -> Disaprove()
//            AffectEnumAll.Disconnection -> Disaprove()
//
//            AffectEnumAll.Anticipation -> Approve()
//            AffectEnumAll.Engagement -> Approve()
//            AffectEnumAll.Esteem -> Approve()
//            AffectEnumAll.Excitement -> Approve()
//
//            AffectEnumAll.Doubt -> Doubt()
//            AffectEnumAll.Confusion -> Doubt()
//            AffectEnumAll.Embarrassment -> Doubt()
//            AffectEnumAll.Fear -> Doubt()
//            AffectEnumAll.Surprise -> Doubt()
//            AffectEnumAll.Yearning -> Doubt()
//
//            AffectEnumAll.Happiness -> Happy()
//            AffectEnumAll.Peace -> Happy()
//            AffectEnumAll.Confidence -> Happy()
//            AffectEnumAll.Pleasure -> Happy()
//            AffectEnumAll.Sympathy -> Happy()
//
//            AffectEnumAll.Nothing -> Unknown()
//        }
//    }
}

enum class EmotionActionEnum {
    goto_next_state,
    smile,
    gaze,
    look_away,
    goto_encourage_state,
    say_again,
    unknown
}

data class ActionTuple(val action: String, val minute: Gesture)

val User.emotion : Emotion
    get() =  Emotion()

public class Emotion {
    val client = DialogManagerClient()

    fun getAction(round_num : Int, user_action : String, user_emotion_idx : Int, agent_action : String) : ActionTuple {
        print("RUNNING THE DIALOG MANAGER IN THE CLIENT!")
        var raw_action = ""
        try{
            raw_action = client.callServer(round_num, user_action, user_emotion_idx, agent_action)
        }catch (e: RuntimeException){
            print("Could not fetch affect. Is the server online? ")
            println(e);
        }

        val action = EmotionActionEnum.valueOf(raw_action);

        print("GOTTEN ACTION: "+ action)

        return when(action){
            EmotionActionEnum.goto_next_state -> ActionTuple(action.toString(), Gestures.Blink)// GoToNextState()
            EmotionActionEnum.goto_encourage_state -> ActionTuple(action.toString(), Gestures.Thoughtful)//GoToEncourage()
            EmotionActionEnum.gaze -> ActionTuple(action.toString(), Gestures.GazeAway)// Gaze()
            EmotionActionEnum.look_away -> ActionTuple(action.toString(), Gestures.CloseEyes)// LookAWay()
            EmotionActionEnum.say_again -> ActionTuple(action.toString(), Gestures.Nod)// SayAgain()
            EmotionActionEnum.smile -> ActionTuple(action.toString(), Gestures.BigSmile)//Smile()
            EmotionActionEnum.unknown -> ActionTuple(action.toString(), Gestures.Blink)//Unknown()
        }
    }
}

enum class AffectEnumAll {
    Affection, Anger, Annoyance, Anticipation, Aversion, Confidence, Disapproval, Disconnection,
    Disquietment, Doubt,Confusion, Embarrassment, Engagement, Esteem, Excitement, Fatigue, Fear,Happiness,
    Pain, Peace, Pleasure, Sadness, Sensitivity, Suffering, Surprise, Sympathy, Yearning, Nothing
}

enum class PuzzleLevels {
    easy,
    medium,
    hard
}

class CurrentUser {
    private var num_of_grade: Int = 0
    private var LevelSet: PuzzleLevels = PuzzleLevels.medium

    fun setGradeNum(num:  Int) {
        this.num_of_grade = num
    }

    fun setLevel(level: PuzzleLevels) {
        this.LevelSet = level;
    }

    fun getLevel() : PuzzleLevels {
        return LevelSet
    }
}

// -- Questions

val score_initted = Score()

val User.score : Score
    get() = score_initted

abstract class Question constructor(
        val total_num : Int,
        val percentage: Int
){
        abstract val question: String
        abstract val explaination: String
        abstract val hint : String
        abstract val answer: Number;

        var tries : Int  = 0
        fun incrementTries(){
            this.tries = this.tries + 1
        }

}

class PercentageOf constructor(val total_num_p  : Int, val percentage_p: Int) : Question(total_num_p, percentage_p) {

    override val answer : Number = ((this.total_num.toDouble() / 100) * this.percentage.toDouble()).toInt()
    override val question : String = "What is " + this.percentage + " percent of " + this.total_num + "?"

    override val explaination : String = "The answer is " +
            this.answer +
            ". You can calculate this by dividing first " +
            this.total_num + " by " +
            100 + ". That is " +
            (this.total_num.toDouble() / 100.toDouble()).toInt() +
            ". Then multiply that with " + this.percentage

    override val hint : String = "You should divide two numbers and multiply it with 100."
}

class WhatPercentage constructor(var total_num_p : Int, var given_number: Int): Question(total_num_p, ((given_number * 100).toDouble() / total_num_p).toInt()){

    override val answer : Number = this.percentage
    override val question: String = "How much percent is " + given_number + " of " + total_num_p + "?"
    override val hint: String = "Percentage is total number divided by the given number, multiplied by 100. "
    override val explaination: String get() = "Divide the total number by the given number and multiply by 100. This means " + this.total_num_p + "divided by " + this.given_number + " is " + this.percentage + " percent"
}

// Easy
var questionsEasy = arrayOf(
        PercentageOf(100, 10),
        WhatPercentage(250, 25),
        PercentageOf(1000, 10),
        WhatPercentage(200, 50),
        PercentageOf(500, 10)
)

// Medium
var questionsMedium = arrayOf(
        PercentageOf(600, 25),
        WhatPercentage(600, 200),
        PercentageOf(300, 60),
        WhatPercentage(400, 80),
        WhatPercentage(800, 40)
)

// Hard
var questionHard = arrayOf(
        PercentageOf(640, 25),
        WhatPercentage(600, 200),
        PercentageOf(300, 60),
        WhatPercentage(400, 80),
        WhatPercentage(800, 20)

)

class Score{

    var num_wrong_questions : Int = 0
    var num_correct_questions : Int = 0
    var num_corrected_questions : Int = 0
    var question_history : MutableList<Question> = ArrayList()
    var current_level : MutableList<PuzzleLevels> = ArrayList()

    fun getCurrentQuestionNumber() : Int = this.num_correct_questions + this.num_wrong_questions

    /**
     * The score is normalized according to the number of questions.
     * Good question = +2 points
     * Corrected question = +1 point
     * Wrong question = 0 point
     */
    fun getScore(): Double {
        val total_questions = getCurrentQuestionNumber()

        if(total_questions == 0){
            return 0.0
        }

        val points = num_correct_questions * 2 + num_corrected_questions

        return points.toDouble() / total_questions.toDouble()
    }

    fun correctAnswer() {
        print("Correct answer")
        this.num_correct_questions++
    }
    fun incorrectAnswer(){
        this.num_wrong_questions++
    }

    fun correctedAnswer(){
        this.num_wrong_questions++
    }


    fun getCurrentLevel() : PuzzleLevels {
        if(this.current_level.size > 0){
            return this.current_level.get(this.current_level.size - 1)
        } else {
            return PuzzleLevels.medium
        }
    }

    fun getCurrentQuestion() : Question {

        println("Question number and question history" + getCurrentQuestionNumber() + "---" + this.question_history.size)

        if(this.question_history.size == 0 || getCurrentQuestionNumber() > (this.question_history.size - 1)){

            val score = this.getScore()
            var level = PuzzleLevels.medium;
            if(score < 0.75){
                level = PuzzleLevels.easy
            } else if (score > 1.25){
                level = PuzzleLevels.hard
            }

            print("It should give now a " + level + " question")
            // Get a new question from the list.
            var question = questionHard.get(getCurrentQuestionNumber())

            if(level == PuzzleLevels.easy){
                question =  questionsEasy.get(getCurrentQuestionNumber())
            } else if(level == PuzzleLevels.medium){
                question =  questionsMedium.get(getCurrentQuestionNumber())
            }

            this.current_level.add(level)
            this.question_history.add(question);
            return question
        }

        return this.question_history.get(this.question_history.size - 1 )

    }
}

