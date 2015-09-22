class smartcity.City {
    att name: String
    rel districts: smartcity.District
}

class smartcity.District {
    att name: String
    rel contact: smartcity.Contact with maxBound 1
    rel sensors: smartcity.Sensor
}

class smartcity.Contact {
    att name: String
    att email: String
}

class smartcity.Sensor {
    att name: String
    rel dvalues: smartcity.DiscreteValues with maxBound 1
    rel cvalues: smartcity.ContinuousValues with maxBound 1
}

class smartcity.DiscreteValues{
    att dvalue1: Double
    att dvalue2: Double
    att dvalue3: Double
    att dvalue4: Double
}


class smartcity.ContinuousValues{
    att cvalue1: Continuous with precision 0.1
    att cvalue2: Continuous with precision 0.1
    att cvalue3: Continuous with precision 0.1
    att cvalue4: Continuous with precision 0.1
}


