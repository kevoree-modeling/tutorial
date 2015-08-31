class smartcity.City {
    att name: String
    rel districts: smartcity.District
}

class smartcity.District {
    att name: String
    rel contact: smartcity.Contact with maxBound 1
    rel sensors: smartcity.Sensor
    rel checker: smartcity.SensorStateChecker with maxBound 1
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
    with inference "GaussianAnomalyDetection"
    att alpha: Double

    dependency temperatureSensor: smartcity.Sensor
    dependency humiditySensor: smartcity.Sensor

    input tempValue "@temperatureSensor | =value"
    input humidityValue "@humiditySensor | =value"

    output state: smartcity.SensorState
}