package cz.mendelu.project.repositorytest

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import cz.mendelu.project.communication.FuelType
import cz.mendelu.project.communication.OverpassAPI
import cz.mendelu.project.communication.OverpassUtils
import cz.mendelu.project.communication.toGasStationItems
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RemoteRepositoryUnitTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: OverpassAPI

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OverpassAPI::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test fetchGasStations returns correct data`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(MockOverpassAPIResponseString)

        mockWebServer.enqueue(mockResponse)

        // Zavoláme API
        val correctQuery = """
                [out:json];
                node["amenity"="fuel"](around:5000,49.1951,16.6068);
                out;
            """.trimIndent()

        val query = OverpassUtils.buildQuery(49.1951, 16.6068)

        assertEquals(correctQuery, query)

        val response = api.fetchGasStations(query)

        assertEquals(true, response.isSuccessful)

        if (response.isSuccessful) {
            val gasStations = response.body()
            assertNotNull(gasStations)
            if (gasStations != null) {
                assertEquals(true, gasStations.elements.size == 32)

                val gasStaionItems = gasStations.toGasStationItems()
                val gasStaion = gasStaionItems[0]

                assertEquals("OMV", gasStaion.name)
                assertEquals(49.1741009, gasStaion.lat,0.0001)
                assertEquals(16.6028335, gasStaion.lon,0.0001)
                assertEquals(listOf(FuelType.UNKNOWN), gasStaion.fuelTypes)
            }

        } else {
            println("API call failed: ${response.code()}")
        }
    }
}

var MockOverpassAPIResponseString = """
    {
      "version": 0.6,
      "generator": "Overpass API 0.7.62.4 2390de5a",
      "osm3s": {
        "timestamp_osm_base": "2025-01-01T16:06:06Z",
        "copyright": "The data included in this document is from www.openstreetmap.org. The data is made available under ODbL."
      },
      "elements": [
        {
          "type": "node",
          "id": 70835516,
          "lat": 49.1741009,
          "lon": 16.6028335,
          "tags": {
            "addr:city": "Brno",
            "addr:housenumber": "11a",
            "addr:postcode": "63900",
            "addr:street": "Heršpická",
            "amenity": "fuel",
            "brand": "OMV",
            "brand:wikidata": "Q168238",
            "compressed_air": "yes",
            "name": "OMV",
            "opening_hours": "PH,Mo-Su 00:00-24:00"
          }
        },
        {
          "type": "node",
          "id": 247752179,
          "lat": 49.1545954,
          "lon": 16.5999086,
          "tags": {
            "amenity": "fuel",
            "brand": "Tesco",
            "brand:wikidata": "Q487494",
            "brand:wikipedia": "en:Tesco Corporation",
            "fuel:diesel": "yes",
            "fuel:e85": "yes",
            "fuel:octane_95": "yes",
            "name": "Tesco Vídeňská",
            "opening_hours": "06:00-24:00",
            "operator": "Tesco Stores ČR a.s.",
            "website": "https://itesco.cz/",
            "wheelchair": "limited"
          }
        },
        {
          "type": "node",
          "id": 265741020,
          "lat": 49.165815,
          "lon": 16.631278,
          "tags": {
            "amenity": "fuel",
            "brand": "Benzina",
            "brand:wikidata": "Q11130894",
            "brand:wikipedia": "cs:Benzina",
            "fuel:GTL_diesel": "yes",
            "fuel:adblue:canister": "yes",
            "fuel:diesel": "yes",
            "fuel:lpg": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "Benzina - Brno, Hněvkovského",
            "opening_hours": "05:00-22:00",
            "operator": "Unipetrol RPA, s.r.o. – Benzina",
            "website": "https://www.benzina.cz/"
          }
        },
        {
          "type": "node",
          "id": 305559726,
          "lat": 49.1617639,
          "lon": 16.6373883,
          "tags": {
            "amenity": "fuel",
            "compressed_air": "no",
            "fuel:GTL_diesel": "yes",
            "fuel:cng": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_95": "yes",
            "name": "Makro",
            "opening_hours": "08:00-22:00",
            "operator": "MAKRO Cash&Carry ČR s.r.o."
          }
        },
        {
          "type": "node",
          "id": 314395247,
          "lat": 49.2189948,
          "lon": 16.5754426,
          "tags": {
            "amenity": "fuel",
            "brand": "Benzina",
            "brand:wikidata": "Q11130894",
            "compressed_air": "yes",
            "fuel:GTL_diesel": "yes",
            "fuel:adblue:canister": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "Benzina - Brno, Královopolská",
            "opening_hours": "24/7",
            "operator": "Unipetrol RPA, s.r.o. – Benzina",
            "website": "https://www.benzina.cz/"
          }
        },
        {
          "type": "node",
          "id": 320936317,
          "lat": 49.2018186,
          "lon": 16.6671449,
          "tags": {
            "amenity": "fuel",
            "brand": "MOL",
            "brand:wikidata": "Q549181",
            "brand:wikipedia": "cs:MOL (holding)",
            "compressed_air": "yes",
            "fuel:diesel": "yes",
            "fuel:lpg": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "name": "MOL, Brno - Líšeň Sedláčkova 2",
            "opening_hours": "24/7",
            "operator": "MOL Česká republika, s.r.o.",
            "website": "https://molcesko.cz/",
            "wheelchair": "yes"
          }
        },
        {
          "type": "node",
          "id": 357879276,
          "lat": 49.1728084,
          "lon": 16.5550758,
          "tags": {
            "amenity": "fuel",
            "fuel:diesel": "yes",
            "fuel:lpg": "yes",
            "fuel:octane_95": "yes",
            "name": "Prim Brno, Starý Lískovec",
            "opening_hours": "06:00-22:00",
            "operator": "PETRA s.r.o.",
            "website": "http://www.csprim.cz/cs-prim-brno-jihlavska.html"
          }
        },
        {
          "type": "node",
          "id": 371359445,
          "lat": 49.1684625,
          "lon": 16.599703,
          "tags": {
            "addr:city": "Brno",
            "addr:housenumber": "758",
            "addr:postcode": "61900",
            "addr:street": "Heršpická",
            "addr:unit": "13",
            "amenity": "fuel",
            "amenity_1": "car_wash",
            "brand": "JEF",
            "compressed_air": "no",
            "fuel:diesel": "yes",
            "fuel:lpg": "yes",
            "fuel:octane_95": "yes",
            "name": "JEF",
            "opening_hours": "24/7",
            "operator": "JEF",
            "phone": "+420 543 244 514",
            "self_service": "no",
            "source": "cuzk:km;streetlevel imagery"
          }
        },
        {
          "type": "node",
          "id": 387226364,
          "lat": 49.1766307,
          "lon": 16.602597,
          "tags": {
            "amenity": "fuel",
            "brand": "Shell",
            "brand:wikidata": "Q110716465",
            "compressed_air": "yes",
            "name": "Shell",
            "opening_hours": "PH,Mo-Su 00:00-24:00",
            "wheelchair": "yes"
          }
        },
        {
          "type": "node",
          "id": 387804440,
          "lat": 49.1846381,
          "lon": 16.6120853,
          "tags": {
            "amenity": "fuel",
            "brand": "EuroOil",
            "compressed_air": "yes",
            "contact:website": "https://www.ceproas.cz/eurooil",
            "fuel:biodiesel": "yes",
            "fuel:diesel": "yes",
            "fuel:e85": "no",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "EuroOil Brno, Opuštěná",
            "opening_hours": "Mo-Su 06:00-20:00",
            "operator": "Čepro, a.s.",
            "ref": "447",
            "shop": "gas",
            "source": "cuzk:km"
          }
        },
        {
          "type": "node",
          "id": 390842385,
          "lat": 49.1751077,
          "lon": 16.5622194,
          "tags": {
            "addr:city": "Brno",
            "addr:housenumber": "530/1",
            "addr:postcode": "63400",
            "addr:street": "Bítešská",
            "amenity": "fuel",
            "brand": "MOL",
            "brand:wikidata": "Q549181",
            "brand:wikipedia": "cs:MOL (holding)",
            "fuel:diesel": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "MOL, Brno - Bítešská",
            "opening_hours": "24/7",
            "operator": "MOL Česká republika, s.r.o.",
            "website": "https://molcesko.cz/"
          }
        },
        {
          "type": "node",
          "id": 470223351,
          "lat": 49.2361999,
          "lon": 16.5870972,
          "tags": {
            "amenity": "fuel",
            "brand": "MOL",
            "brand:wikidata": "Q549181",
            "brand:wikipedia": "cs:MOL (holding)",
            "check_date:opening_hours": "2024-06-03",
            "compressed_air": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "MOL, Řečkovice - Palackého",
            "opening_hours": "Mo-Fr 06:00-22:00, Sa-Su 07:00-22:00",
            "operator": "MOL Česká republika, s.r.o.",
            "source": "cuzk:km",
            "website": "https://molcesko.cz/"
          }
        },
        {
          "type": "node",
          "id": 579743467,
          "lat": 49.2080242,
          "lon": 16.6474198,
          "tags": {
            "addr:city": "Brno",
            "addr:street": "Rokytova",
            "amenity": "fuel",
            "fuel:diesel": "yes",
            "fuel:octane_95": "yes",
            "name": "Prim Brno, Židenice",
            "opening_hours": "06:00-22:00",
            "operator": "PETRA s.r.o.",
            "website": "http://www.csprim.cz/cs-prim-brno-rokytova.html"
          }
        },
        {
          "type": "node",
          "id": 843845753,
          "lat": 49.1751844,
          "lon": 16.5831809,
          "tags": {
            "amenity": "fuel",
            "compressed_air": "no",
            "fuel:diesel": "yes",
            "fuel:octane_95": "yes",
            "name": "Pneuservis Bílý & syn s.r.o."
          }
        },
        {
          "type": "node",
          "id": 848840602,
          "lat": 49.1766771,
          "lon": 16.5750871,
          "tags": {
            "amenity": "fuel"
          }
        },
        {
          "type": "node",
          "id": 917366646,
          "lat": 49.1654547,
          "lon": 16.6298124,
          "tags": {
            "amenity": "fuel",
            "brand": "Benzina",
            "brand:wikidata": "Q11130894",
            "brand:wikipedia": "cs:Benzina",
            "compressed_air": "yes",
            "fuel:GTL_diesel": "yes",
            "fuel:adblue:canister": "yes",
            "fuel:cng": "yes",
            "fuel:diesel": "yes",
            "fuel:lpg": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "Benzina - Brno, Kaštanová",
            "opening_hours": "24/7",
            "operator": "Unipetrol RPA, s.r.o. – Benzina",
            "website": "https://www.benzina.cz/"
          }
        },
        {
          "type": "node",
          "id": 1608100741,
          "lat": 49.1878767,
          "lon": 16.6228929,
          "tags": {
            "amenity": "fuel",
            "brand": "Benzina",
            "brand:wikidata": "Q11130894",
            "compressed_air": "yes",
            "fuel:GTL_diesel": "yes",
            "fuel:adblue:canister": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "Benzina - Brno Zvonařka",
            "opening_hours": "24/7",
            "operator": "Unipetrol RPA, s.r.o. – Benzina",
            "website": "https://www.benzina.cz/"
          }
        },
        {
          "type": "node",
          "id": 2660337809,
          "lat": 49.1730012,
          "lon": 16.5509313,
          "tags": {
            "amenity": "fuel",
            "name": "LPG"
          }
        },
        {
          "type": "node",
          "id": 2732886370,
          "lat": 49.2158107,
          "lon": 16.6021212,
          "tags": {
            "addr:city": "Brno",
            "addr:housenumber": "1a",
            "addr:postcode": "61200",
            "addr:street": "U Červeného mlýna",
            "amenity": "fuel",
            "compressed_air": "no",
            "fuel:lpg": "yes",
            "name": "SDIL.CZ LPG",
            "operator": "SDIL Building Automotive",
            "self_service": "yes"
          }
        },
        {
          "type": "node",
          "id": 2916588754,
          "lat": 49.2003672,
          "lon": 16.6135069,
          "tags": {
            "amenity": "fuel",
            "name": "IBC"
          }
        },
        {
          "type": "node",
          "id": 3409526427,
          "lat": 49.1834134,
          "lon": 16.6714543,
          "tags": {
            "amenity": "fuel",
            "brand": "Benzina",
            "brand:wikidata": "Q11130894",
            "compressed_air": "yes",
            "fuel:GTL_diesel": "yes",
            "fuel:adblue:canister": "yes",
            "fuel:cng": "yes",
            "fuel:diesel": "yes",
            "fuel:lpg": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "Benzina - Brno, Slatina",
            "opening_hours": "24/7",
            "operator": "Unipetrol RPA, s.r.o. – Benzina",
            "website": "https://www.benzina.cz/"
          }
        },
        {
          "type": "node",
          "id": 3951327351,
          "lat": 49.1903759,
          "lon": 16.6318935,
          "tags": {
            "amenity": "fuel",
            "brand": "MOL",
            "brand:wikidata": "Q549181",
            "brand:wikipedia": "en:MOL (company)",
            "compressed_air": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "MOL, Brno - Olomoucká",
            "opening_hours": "05:00 - 22:00",
            "operator": "MOL Česká republika, s.r.o.",
            "website": "https://molcesko.cz/"
          }
        },
        {
          "type": "node",
          "id": 4300517889,
          "lat": 49.1845939,
          "lon": 16.6599416,
          "tags": {
            "amenity": "fuel",
            "compressed_air": "no",
            "name": "Samoobslužná čerpací stanice",
            "name:en": "Self-service gas station",
            "opening_hours": "Mo-Su 05:00-22:15"
          }
        },
        {
          "type": "node",
          "id": 4853159020,
          "lat": 49.1834882,
          "lon": 16.5796553,
          "tags": {
            "amenity": "fuel",
            "brand": "MOL",
            "brand:wikidata": "Q549181",
            "brand:wikipedia": "en:MOL (company)",
            "fuel:adblue": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "name": "MOL, Brno - Pisárky Bauerova",
            "opening_hours": "24/7",
            "operator": "MOL Česká republika, s.r.o.",
            "website": "https://molcesko.cz/"
          }
        },
        {
          "type": "node",
          "id": 4853159221,
          "lat": 49.2318702,
          "lon": 16.5826231,
          "tags": {
            "amenity": "fuel",
            "brand": "MOL",
            "brand:wikidata": "Q549181",
            "brand:wikipedia": "cs:MOL (holding)",
            "compressed_air": "yes",
            "fuel:GTL_diesel": "yes",
            "fuel:HGV_diesel": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "MOL Brno - Hradecká",
            "opening_hours": "06:00 - 22:00",
            "operator": "MOL Česká republika, s.r.o.",
            "website": "https://molcesko.cz/"
          }
        },
        {
          "type": "node",
          "id": 5318967196,
          "lat": 49.1835434,
          "lon": 16.6699089,
          "tags": {
            "amenity": "fuel",
            "fuel:cng": "yes",
            "operator": "DPMB"
          }
        },
        {
          "type": "node",
          "id": 5599873947,
          "lat": 49.2326703,
          "lon": 16.6213782,
          "tags": {
            "amenity": "fuel",
            "brand": "EuroOil",
            "car_wash": "yes",
            "check_date:opening_hours": "2023-08-27",
            "compressed_air": "yes",
            "contact:website": "https://www.ceproas.cz/eurooil",
            "fuel:diesel": "yes",
            "fuel:e85": "yes",
            "fuel:octane_95": "yes",
            "fuel:octane_98": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "EuroOil Brno - Lesná, Okružní",
            "opening_hours": "Mo-Su 06:00-21:00",
            "operator": "Čepro, a.s.",
            "ref": "403",
            "shop": "gas"
          }
        },
        {
          "type": "node",
          "id": 5614142959,
          "lat": 49.1875581,
          "lon": 16.5872637,
          "tags": {
            "amenity": "fuel",
            "brand": "EuroOil",
            "brand:wikidata": "Q110219457",
            "contact:website": "https://www.ceproas.cz/eurooil",
            "fuel:diesel": "yes",
            "fuel:e85": "no",
            "fuel:octane_95": "yes",
            "fuel:octane_98": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "EuroOil Brno - výstaviště, Křížkovského",
            "opening_hours": "Mo-Su 06:00-22:00",
            "operator": "Čepro, a.s.",
            "ref": "404"
          }
        },
        {
          "type": "node",
          "id": 7561396255,
          "lat": 49.2212463,
          "lon": 16.5828147,
          "tags": {
            "amenity": "fuel",
            "brand": "Orlen",
            "brand:wikidata": "Q971649",
            "compressed_air": "yes",
            "contact:facebook": "https://www.facebook.com/orlencz",
            "fuel:GTL_diesel": "yes",
            "fuel:adblue:canister": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_100": "yes",
            "fuel:octane_95": "yes",
            "internet_access": "wlan",
            "internet_access:fee": "no",
            "name": "Orlen",
            "opening_hours": "24/7",
            "operator": "Orlen Unipetrol RPA s.r.o. - Benzina, odštěpný závod",
            "website": "https://www.orlen.cz/",
            "wheelchair": "yes"
          }
        },
        {
          "type": "node",
          "id": 9830893177,
          "lat": 49.215864,
          "lon": 16.6023286,
          "tags": {
            "amenity": "fuel",
            "compressed_air": "no",
            "name": "BETA EKO",
            "opening_hours": "24/7",
            "self_service": "yes"
          }
        },
        {
          "type": "node",
          "id": 10704025589,
          "lat": 49.1615403,
          "lon": 16.619301,
          "tags": {
            "amenity": "fuel",
            "fuel:adblue": "yes",
            "fuel:diesel": "yes",
            "fuel:octane_95": "yes",
            "name": "Hortim"
          }
        },
        {
          "type": "node",
          "id": 12237243479,
          "lat": 49.195235,
          "lon": 16.6239311,
          "tags": {
            "amenity": "fuel",
            "fuel": "lpg",
            "self_service": "yes"
          }
        }
      ]
    }
""".trimIndent()
