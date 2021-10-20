package furhatos.app.mathtutor
import furhatos.app.mathtutor.flow.*
import furhatos.event.Event
import furhatos.records.User
import java.lang.Exception

val User.info : CurrentUser
    get() = data.getOrPut(CurrentUser::class.qualifiedName, CurrentUser())

//val User.emotion : Event
//    get () = Affect().getAffect()
//
////public class Affect {
//    val client = SocketClient()
//
//    public fun getAffect() : Event {
//        var emotion  = ""
//        try{
//            emotion = client.callServer()
//        }catch (e: RuntimeException){
//            print("Could not fetch emotion. Is the server online? ")
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
//}
//
//enum class AffectEnumAll {
//    Affection, Anger, Annoyance, Anticipation, Aversion, Confidence, Disapproval, Disconnection,
//    Disquietment, Doubt,Confusion, Embarrassment, Engagement, Esteem, Excitement, Fatigue, Fear,Happiness,
//    Pain, Peace, Pleasure, Sadness, Sensitivity, Suffering, Surprise, Sympathy, Yearning, Nothing
//}

class CurrentUser {
    private var num_of_grade: Int = 0

    fun setGradeNum(num:  Int) {
        this.num_of_grade = num
    }
}

