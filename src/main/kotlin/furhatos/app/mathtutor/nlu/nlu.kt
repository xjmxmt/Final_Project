package furhatos.app.mathtutor.nlu

import furhatos.nlu.Intent
import furhatos.util.Language
import furhatos.nlu.common.Number

class Confused : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("Who are you?", "Where am I?", "What?", "What is this?", "What the hell is this?", "I don't know")
    }
}

class OnePer : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("one percent", "1%")
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

