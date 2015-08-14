class smartcity.City {
    att name: String
    ref* districts: smartcity.District
}

class smartcity.District {
    att name: String
    ref contact: smartcity.Contact
    ref* sensors: smartcity.Sensor
    ref checker: smartcity.SensorStateChecker
}

class smartcity.Contact {
    att name: String
    att email: String
}

class smartcity.Sensor {
    att name: String
    att value: Double
}

enum smartcity.SensorState {
    CORRECT, SUSPICIOUS
}

class smartcity.SensorStateChecker {
    with inference "GaussianClassifier"
    dependency evaluatedSensor: smartcity.Sensor
    input value "@evaluatedSensor | =value"
    output state: smartcity.SensorState
}