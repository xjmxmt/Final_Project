package furhatos.app.mathtutor
import furhatos.app.mathtutor.flow.*
import furhatos.event.Event
import furhatos.records.User
import java.lang.Exception

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

        return when(affection){
            AffectEnumAll.Affection -> Annoyed()
            AffectEnumAll.Anger -> Annoyed()
            AffectEnumAll.Annoyance -> Annoyed()
            AffectEnumAll.Aversion -> Annoyed()
            AffectEnumAll.Disquietment -> Annoyed()
            AffectEnumAll.Fatigue -> Annoyed()
            AffectEnumAll.Pain -> Annoyed()
            AffectEnumAll.Sadness -> Annoyed()
            AffectEnumAll.Sensitivity -> Annoyed()
            AffectEnumAll.Suffering -> Annoyed()

            AffectEnumAll.Disapproval -> Disaprove()
            AffectEnumAll.Disconnection -> Disaprove()

            AffectEnumAll.Anticipation -> Approve()
            AffectEnumAll.Engagement -> Approve()
            AffectEnumAll.Esteem -> Approve()
            AffectEnumAll.Excitement -> Approve()

            AffectEnumAll.Doubt -> Doubt()
            AffectEnumAll.Confusion -> Doubt()
            AffectEnumAll.Embarrassment -> Doubt()
            AffectEnumAll.Fear -> Doubt()
            AffectEnumAll.Surprise -> Doubt()
            AffectEnumAll.Yearning -> Doubt()

            AffectEnumAll.Happiness -> Happy()
            AffectEnumAll.Peace -> Happy()
            AffectEnumAll.Confidence -> Happy()
            AffectEnumAll.Pleasure -> Happy()
            AffectEnumAll.Sympathy -> Happy()

            AffectEnumAll.Nothing -> Unknown()
        }
    }
}

enum class AffectEnumAll {
    Affection, Anger, Annoyance, Anticipation, Aversion, Confidence, Disapproval, Disconnection,
    Disquietment, Doubt,Confusion, Embarrassment, Engagement, Esteem, Excitement, Fatigue, Fear,Happiness,
    Pain, Peace, Pleasure, Sadness, Sensitivity, Suffering, Surprise, Sympathy, Yearning, Nothing
}

class CurrentUser {
    private var num_of_grade: Int = 0

    fun setGradeNum(num:  Int) {
        this.num_of_grade = num
    }
}

// -- Questions

val score_initted = Score()

val User.score : Score
    get() = score_initted

class Question constructor(
        var total_num : Number,
        var percentage: Number
) {
    val answer : Number = ((this.total_num.toDouble() / 100) * this.percentage.toDouble()).toInt()
    var question : String = "What is " + this.percentage + " percent of " + this.total_num + "?" //TODO make nice sentence.
    var tries : Int = 0

    var explaination : String = "The answer is " +
            this.answer +
            ". You can calculate this by dividing first " +
            this.total_num + " by " +
            this.percentage + ". That is " +
            (this.total_num.toDouble() / this.percentage.toDouble()) +
            ". Then multiply that with 100."

    fun incrementTries(){
        this.tries = this.tries + 1
    }

}

var questionsInnited = arrayOf(
        Question(100, 10),
        Question(1000, 10),
        Question(500, 10),
        Question(500, 20),
        Question(250, 25)
)

val User.questions : Array<Question>
    get() = questionsInnited

val User.current_question : Question
    get() = questions.get(score.getCurrentQuestionNumber())

class Score{

    var points: Number = 10
    var num_wrong_questions : Int = 0
    var num_correct_questions : Int = 0

    fun getCurrentQuestionNumber() : Int = this.num_correct_questions + this.num_wrong_questions

    fun correctAnswer() {
        print("Correct answer")
        this.num_correct_questions++
    }
    fun incorrectAnswer(){
        print("Before points: " + this.points)
        this.points =- 2
        this.num_wrong_questions++
        print("After points: " + this.points)
    }

    fun correctedAnswer(){
        print("Before points: " + this.points)
        this.points =+ 1
        this.num_wrong_questions++
        print("After points: " + this.points)
    }
}

