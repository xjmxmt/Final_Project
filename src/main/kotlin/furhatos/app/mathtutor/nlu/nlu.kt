package furhatos.app.mathtutor.nlu

import furhatos.nlu.EnumEntity
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
        return listOf("one percent", "1%", "one", "1")
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


class QuestionAnswer (
        val count : Number = Number(0)
) : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return listOf("@count", "That is @count", "The answer is @count", "Its @count","1", "2", "3", "4", "5","6","7","8", "9", "10", "100", "200", "300", "400", "500", "1000", "10000")
    }

    fun getAnswer(): Number {
        return count
    }
}

class Swearing(val swear_word : SwearWords? = null) : Intent() {

    override fun getExamples(lang: Language): List<String> {
        return listOf(
                "@swear_word ")
    }
}


class SwearWords : EnumEntity(stemming = true, speechRecPhrases = true) {
    override fun getEnum(lang: Language): List<String> {
        return listOf("arse",
                "ass",
                "asshole",
                "bastard",
                "bitch",
                "bollocks",
                "brotherfucker",
                "bugger",
                "bullshit",
                "child-fucker",
                "Christ on a bike",
                "Christ on a cracker",
                "cocksucker",
                "crap",
                "cunt",
                "damn",
                "dick",
                "effing",
                "fatherfucker",
                "frigger",
                "fuck",
                "goddamn",
                "godsdamn",
                "hell",
                "holy shit",
                "horseshit",
                "Jesus Christ",
                "Jesus fuck",
                "Jesus H. Christ",
                "Jesus Harold Christ",
                "Jesus wept",
                "Jesus, Mary and Joseph",
                "Judas Priest",
                "motherfucker",
                "nigga",
                "piss",
                "prick",
                "shit",
                "shit ass",
                "shitass",
                "sisterfucker",
                "slut",
                "son of a bitch",
                "son of a whore",
                "sweet Jesus",
                "twat"
        )
    }
}


