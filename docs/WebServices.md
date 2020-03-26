<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright IBM Corp. 2007-2020 -->

# WebServices.csv

## Columns

1. **CS?** - indicates whether the line should be considered as a template (true) or a single object
1. **WebServices Name** - name of the web-service
1. **WebService Description** - a brief description of the functionality provided by the web-service
1. **Protocol** - protocol by which the web-service is invoked (currently only `SOAP_HTTP`)
1. **Style** - invocation style of the web-service; either `RPC_ENCODED` or `DOCUMENT_LITERAL`
1. **WSDL File** - name of the wsdl file that provides the definition of the web-service; should be located under "/archives/wsdl/" within Scripts.csv
1. **Implementation Script** - script responsible for implementing the functionality of the web-service; should be located under "/scripts/wbs/" within Scripts.csv
1. **Store Requests?** - indicates whether this web-service should store its requests (true) or not
1. **Store Responses?** - indicates whether this web-service should store its responses (true) or not
1. **Deploy?** - indicates whether this web-service should be deployed (true) or not

----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright IBM Corp. 2007-2020.
