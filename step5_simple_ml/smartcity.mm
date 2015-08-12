class smartcity.City {
    att name : String
    ref* districts: smartcity.District
}

class smartcity.District {
    att name: String
    att electricityConsumption: Continuous with precision 0.5
    ref contact: smartcity.Contact
    ref* sensors: smartcity.Sensor
}

class smartcity.Contact {
    att name: String
    att email: String
}

class smartcity.Sensor {
    att name: String
    att value: Double
}
