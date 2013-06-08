package org.firepick.firebom;
/*
    Copyright (C) 2013 Karl Lew <karl@firepick.org>. All rights reserved.
    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
    
    This file is part of FirePick Software.
    
    FirePick Software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FirePick Software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FirePick Software.  If not, see <http://www.gnu.org/licenses/>.
    
    For more information about FirePick Software visit http://firepick.org
 */

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class McMasterCarrPart extends Part {
    private static Pattern startPrice = Pattern.compile("\"PrceTxt\":\"");
    private static Pattern endPrice = Pattern.compile("\"");
    private static Pattern startPackageUnits = Pattern.compile("\"SellStdPkgQty\":");
    private static Pattern endPackageUnits = Pattern.compile(",");
    private static String queryUrlTemplate =
            "http://www.mcmaster.com/WebParts/Ordering/InLnOrdWebPart/InLnOrdWebPart.aspx?cntnridtxt=InLnOrd_ItmBxRw_1_{PART}&partnbrtxt={PART}&multipartnbrind=false&partnbrslctdmsgcntxtnm=FullPrsnttn&autoslctdind=false";

    public McMasterCarrPart(PartFactory partFactory, URL url) throws IOException {
        super(partFactory);
        setUrl(url);
    }

    @Override
    protected void update() throws IOException {
        String partNum = getUrl().toString().replace("http://www.mcmaster.com/#", "");
        String queryUrl = queryUrlTemplate.replaceAll("\\{PART\\}", partNum);
        String content = partFactory.urlTextContent(new URL(queryUrl));
        String price = partFactory.scrapeText(content, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String packageUnits = partFactory.scrapeText(content, startPackageUnits, endPackageUnits);
        if (packageUnits != null) {
            setPackageUnits(Double.parseDouble(packageUnits));
        }
        setId(partNum);
    }
}
