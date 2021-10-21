package furhatos.app.mathtutor.nlu

import furhatos.nlu.Intent
import furhatos.util.Language
import furhatos.nlu.common.Number

class Confused : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Who are you?", "Where am I?", "What?", "What is this?", "What the hell is this?")
    }
}

class Learn: Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("I would like to learn some math", "I want to learn", "learn")
    }
}

class Practice: Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("I want to practice", "I want to have some practice", "practice")
    }
}

class Explanations: Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("Can you explain", "I would like an explanation on", "explain", "explanation")
    }
}

class GradeNum: Intent(){
    var gradeNum: Number = Number(0)
    override fun getExamples(lang: Language): List<String> {
        return listOf("@gradeNum", "I'm in grade @gradeNum")
    }
}

class Ready: Intent(){
    override fun getExamples(lang: Language): List<String> {
        return listOf("I'm ready", "ready")
    }
}

class Repeat : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf(
                "What was the question again?",
                "Can you repeat?",
                "repeat",
                "question again",
                "What was the question",
                "Question?"
        )
    }
}

class QuestionAnswer : Number()

class Number (
        val count : Number
) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@count", "That is @count", "The answer is @count", "Its @count","1", "2", "3", "4", "5","6","7","8", "9", "10", "100", "200", "300", "400", "500", "1000", "10000")
    }

    fun toText(): String {
        return count.toString()
    }
}



