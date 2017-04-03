# Accelero2
### This app compares average gait cycle of both legs and represents it on a scale from 0 to MAX_VALUE.<br>
### Smartphone embedded accelerometer is used to take readings voluntary by the user in the app.<br>
### Smartphone is kept in the front pocket of jeans with fixed orientation such that +Y axis and +Z axis faces top and front/forward direction respectively and Z-axis reading is processed.<br>
### Accelerometer raw readings are saved in the .txt format so that it can be processed or cross-verified on the computer later, acting as a data logger.<br>
### The raw data is filtered by a low-pass(LP) filter.<br>
### LP filtered data passes via moving average filtered data x(default 5) times with a window size of x(default 5) to obtain a smooth graph.<br>
### Then the data is normalized.<br>
### Points of local maxima are recorded, and then points with a value less than a threshold value is discarded for each leg data set, thus giving us gait cycle points.<br>
### Average Gait Cycle(AGC) is calculated for each leg separately, that is the gait cycle template which has the closest resemblance to other gait cycles using dynamic time wrapping(dwt).<br>
### Average Gait Cycle Template calculated in previous step for each leg is compared using dynamic time wrapping(DWT), the value obtained is reported.<br>
