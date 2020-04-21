# LTE Coverage Tool

## PURPOSE
This experimental application was developed by the National Institute of Standards and Technology (NIST) Public Safety Communications Research Division (PSCR), with funding from the Department of Homeland Security Science & Technology Directorate (DHS S&T).  Its purpose is to enable public safety personnel to measure general LTE coverage quality using a standard smartphone.  Source code is published as a software development kit (SDK) to allow developers to enhance the application or use it in other applications.
 
The LTE Coverage Tool application and SDK enable first responders and public safety personnel to survey and evaluate coverage by LTE networks in environments where incidents are ongoing or planned.  While accuracy of the data from UE measurements is much lower than that of specialized equipment, the application provides subjective assessments of coverage quality, using UEs which are already available to agencies and personnel.

Release of the software application and SDK is the culmination of several years of research efforts under the In-Building Coverage Quality Measurement Tool project, conducted by NIST PSCR and the National Telecommunications and Information Administration (NTIA) Institute for Telecommunication Sciences (ITS).  The goal of that project was to determine whether a UE (i.e., LTE phone) experimental application could be used to provide a reliable assessment of LTE coverage.  The final report, which will be released by NTIA ITS in 2019, determined that "non-experts could quickly master the operation of this system and obtain results that are statistically equivalent to expert propagation measurement engineers.”

DHS S&T instructed NIST PSCR to release the software in order to allow first responders to access and use the application, and external developers to use the source code as a base for further development.

## OBTAINING SOFTWARE
* Source code available at:  https://github.com/usnistgov/LTECoverageTool/
* Android application available at:  https://play.google.com/store/apps/details?id=gov.nist.oism.asd.ltecoveragetool
* NTIA Report 18-XXX "In-Building LTE Coverage Measurements Using Public-Safety Android Phones" available at:  (link to be inserted when document is available, expected in 2020)

## HARDWARE/SOFTWARE REQUIREMENTS
Android device, software version 4.4 (KitKat, API level 19) through version 10 (API level 29).

## INSTALLING SOFTWARE
Download application from Google Play Store, following prompts.

## RUNNING SOFTWARE (APPLICATION)
* Start application from the LTE Coverage Tool icon.
* Read and accept disclaimers.
* Enter an offset value in dB if needed to raise the reported value for RSRP (intended for use where a device has a known trend for under-reporting measurements/quality).
* Press the "New Recording" button to start an assessment, and move through the area to be surveyed.
* Use the "Pause" button if needed to stop measurements temporarily while keeping a session open; use the "Resume" button to continue.
* When the assessment is completed, press the "Stop" button.
* The "Grade" tab will display a grade between 1 and 10 for the route traversed during the session (where 10 is the best possible grade) and a pie chart showing the percentage of points which fell into poor, good or excellent categories.
* The "Line Chart" tab displays measurements of RSRP for the session.
* Save screen shots as needed for later reference, since the pie chart and graph are not available after the user backs out of the results tab or stops the application.
* Note that the user is directed to access 3GPP and other online references for interpreting data values, while considering the uncertainty statements in the application.
* For post processing, the application saves comma-separated variables (CSV) files with data points as a function of time.  The file path may vary per device, but a typical path is "Android/data/gov.nist.oism.asd.ltecoveragetool/files/Sep_18_2018_1:30:58_PM.csv".

## RUNNING SOFTWARE (DEVELOPMENT)
* The GitHub repository contains all source code files required to open the project in Android Studio for further development.

## MEMORY AND STORAGE
* Installation to a device SD card is allowed by the current software package, but that capability has not been tested.
* Typical storage requirement for the application is approximately 7 MB, but the total requirement will increase as CSV files are created.
* CSV log files created by the application (see above) are relatively small, but if the application is used extensively, occasional manual deletion may be beneficial.
* Maximum observed memory usage during testing was 66 MB (Pixel 2, Android version 9). 

## TECHNICAL SUPPORT
For more information concerning the LTE Coverage Tool application and SDK, please contact pscr@nist.gov.

## VERSION 1.2.0 NOTES
Version 1.2.0 includes the following fixes or enhancements:
* If a user presses the back button during recording, data is lost (reported in V1.02).  Dialog is added to confirm back operation during recording and warn user that data will be lost.
* Application stops reporting new measurements (reported in V1.02).  Incorporated new approach for querying LTE information while recording to avoid reporting stale data.
* Application stops recording in background (e.g., when notification is received by UE).  Changed flag to allow continued running/recording in background.
* Added permissions request to use LTE functionality (access to location data required for querying LTE values directly from Android).

## VERSION 1.3.2 NOTES
Version 1.2.0 is not compatible with Android 10 (API version 29) and above, therefore Version 1.3.2 was introduced to use Android Q.  Android 10 changed the way the SignalStrenth object returns the value which causes a parse exception, required calling requestCellInfoUpdate to avoid stale data, and it requires additional permissions.  Version 1.3.2 includes the following fixes or enhancements:
* Added ACCESS_FINE_LOCATION permissions (api 29)
* Moved all view compatibility libraries from legacy to the new AndroidX compatibility libraries (api 29)
* Called for refresh of cell info data for api 29 and higher
* Parsed the cell info data differently for api 29 and higher

## KNOWN ISSUES
Testing was conducted on 5 UE models from 4 vendors with 5 Android versions; vendor names are not provided below.  The following issues have been reported and may be considerd for future releases:
* On UE with small screen, "(dBm)*" wraps on measurement lines for RSRP and RSRQ.  Consider using layouts for screen sizes on future release.
* On UE with small screen, text wraps in Pause, Stop and Resume buttons.  Buffer could be decreased at sides, but it would expand buttons on larger screens also.  Edge of button would be close to edge of device, with extremely long buttons on large screens.  Alternate layouts would be a fix.
* Installation on SD card was not tested.
* One UE on Android v8.0.0 stops updating measurements under changing RF conditions, consistenly for a few seconds and occasionally observed for over 3.5 minutes.  Confirmed the same UE model/version showed the problem on app version V1.02 (new UE was not available in 2018 during initial testing).
* One UE on Android V4.4.4 continues reporting last measurements and service level (e.g., "Good" or "Poor") when the UE goes out of coverage; the app never reported "No Signal."  Upon reacquiring the LTE signal, measurements resume.  This is reproducible on the the first UE (with Android V4.4.4) and was observed twice on another model UE (Android 6.0.1).
* RSRQ is reported in dBm and should be reported in dB.

## Disclaimer of Liability Notice
The United States Government shall not be liable or responsible for any maintenance, updating or for correction of any errors in the software.

THIS SOFTWARE IS PROVIDED “AS IS” WITHOUT ANY WARRANTY OF ANY KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT THE DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SOFTWARE.  IN NO EVENT SHALL THE UNITED STATES GOVERNMENT OR ITS CONTRACTORS OR SUBCONTRACTORS BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR IN ANY WAY CONNECTED WITH THE SOFTWARE OR ANY OTHER PROVIDED DOCUMENTATION, WHETHER OR NOT BASED UPON WARRANTY, CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR ANY PROVIDED DOCUMENTATION. THE UNITED STATES GOVERNMENT DISCLAIMS ALL WARRANTIES AND LIABILITIES REGARDING THIRD PARTY SOFTWARE, IF PRESENT IN THE SOFTWARE, AND DISTRIBUTES IT “AS IS.”

LICENSEE AGREES TO WAIVE ANY AND ALL CLAIMS AGAINST THE U.S. GOVERNMENT AND THE UNITED STATES GOVERNMENT’S CONTRACTORS AND SUBCONTRACTORS, AND SHALL INDEMNIFY AND HOLD HARMLESS THE U.S. GOVERNMENT AND THE UNITED STATES GOVERNMENT’S CONTRACTORS AND SUBCONTRACTORS FOR ANY LIABILITIES, DEMANDS, DAMAGES, EXPENSES, OR LOSSES THAT MAY ARISE FROM RECIPIENT’S USE OF THE SOFTWARE OR PROVIDED DOCUMENTATION, INCLUDING ANY LIABILITIES OR DAMAGES FROM PRODUCTS BASEDON, OR RESULTING FROM, THE USE THEREOF.

## PROHIBITION ON USE OF DHS IDENTITIES NOTICE
A.  No user shall use the DHS or its component name, seal or other identity, or any variation or adaptation thereof, for an enhancement, improvement, modification or derivative work utilizing the software.

B.  No user shall use the DHS or its component name, seal or other identity, or any variation or adaptation thereof for advertising its products or services, with the exception of using a factual statement such as included in the ACKNOWLEDGEMENT NOTICE indicating DHS funding of development of the software.

C.  No user shall make any trademark claim to the DHS or its component name, seal or other identity, or any other confusing similar identity, and no user shall seek registration of these identities at the U.S. Patent and Trademark Office.

## ACKNOWLEDGEMENT NOTICE
This software was developed with funds from the Department of Homeland Security’s Science and Technology Directorate.

## NIST Software
This software was developed by employees of the National Institute of Standards and Technology (NIST), an agency of the Federal Government and is being made available as a public service. Pursuant to title 17 United States Code Section 105, works of NIST employees are not subject to copyright protection in the United States.  This software may be subject to foreign copyright.  Permission in the United States and in foreign countries, to the extent that NIST may hold copyright, to use, copy, modify, create derivative works, and distribute this software and its documentation without fee is hereby granted on a non-exclusive basis, provided that this notice and disclaimer of warranty appears in all copies.

THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT, INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY, CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.

## Uncertainty Statement
Values are assumed to be accurate within ±8 dB for RSRP or ±3.5 dB for RSRQ, for a UE complying with 3GPP standard TR36.133 sections 9.1.2, 9.1.3, 9.1.5 and 9.1.6, operating under the following conditions:

1. Cell specific reference signals are transmitted either from one, two or four antenna ports.
2. Conditions defined in 36.101 Clause 7.3 for reference sensitivity are fulfilled.
3. RSRP|dBm according to Annex B.3.1 or B.3.3, as appropriate, for a corresponding Band.
4. Normal condition, as defined by 3GPP.
