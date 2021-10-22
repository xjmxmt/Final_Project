package furhatos.app.mathtutor
import furhatos.app.mathtutor.flow.*
import furhatos.event.Event
import furhatos.records.User
import java.lang.Exception
import java.util.*

val User.info : CurrentUser
    get() = data.getOrPut(CurrentUser::class.qualifiedName, CurrentUser())

val User.emotion : Event
    get () = Affect().getAffect()

public class Affect {
    val client = SocketClient()

    public fun getAffect() : Event {
        var emotion  = ""
        try{
            emotion = client.callServer()
        }catch (e: RuntimeException){
            print("Could not fetch emotion. Is the server online? ")
            println(e);

        }

        var affection = AffectEnumAll.Nothing
        try{
            affection = AffectEnumAll.valueOf(emotion);
        }catch (e : Exception){
            print("Could not find return value inside the defined Affect enum. ")
        }

        val registeredEmotion = when(affection){

            AffectEnumAll.Anger -> Angry()
            AffectEnumAll.Sadness -> Angry()
            AffectEnumAll.Pain -> Angry()

            AffectEnumAll.Annoyance -> Annoyed()
            AffectEnumAll.Aversion -> Annoyed()
            AffectEnumAll.Disquietment -> Annoyed()
            AffectEnumAll.Fatigue -> Annoyed()

            AffectEnumAll.Sensitivity -> Annoyed()

            AffectEnumAll.Disapproval -> Annoyed()
            AffectEnumAll.Disconnection -> Annoyed()

            AffectEnumAll.Suffering -> Doubt()
            AffectEnumAll.Doubt -> Doubt()
            AffectEnumAll.Confusion -> Doubt()
            AffectEnumAll.Embarrassment -> Doubt()
            AffectEnumAll.Fear -> Doubt()
            AffectEnumAll.Surprise -> Doubt()
            AffectEnumAll.Yearning -> Doubt()

            AffectEnumAll.Anticipation -> Positive()
            AffectEnumAll.Engagement -> Positive()
            AffectEnumAll.Esteem -> Positive()
            AffectEnumAll.Excitement -> Positive()
            AffectEnumAll.Affection -> Positive()
            AffectEnumAll.Happiness ->Positive()
            AffectEnumAll.Peace -> Positive()
            AffectEnumAll.Confidence -> Positive()
            AffectEnumAll.Pleasure -> Positive()
            AffectEnumAll.Sympathy -> Positive()

            AffectEnumAll.Nothing -> Unknown()
        }
        print("Emotion from the server: (mapped-emotion, server-emotion)"+ registeredEmotion.event_name + ", ==== " + emotion)
        return registeredEmotion
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

    var skip_intro: Boolean = false
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

    fun initHardQuestion(){
        if(question_history.size == 0){
            question_history.add(questionHard.get(0))
            current_level.add(PuzzleLevels.hard)
        } else {
            print("Init level was already set to HARD")
        }

    }

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

