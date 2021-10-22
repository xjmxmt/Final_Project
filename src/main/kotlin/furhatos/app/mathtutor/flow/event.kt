package furhatos.app.mathtutor.flow

import furhatos.event.Event

class Unknown : Event() // 0
class Positive  : Event() // 0
class Doubt : Event() // 1
class Annoyed : Event() // 2
class Angry : Event() // 4

// Agent emotion
class GoToNextState: Event()
class Smile: Event()
class Gaze: Event()
class LookAWay: Event()
class Encourage: Event()
class SayAgain: Event()
class GoToEncourage: Event()
