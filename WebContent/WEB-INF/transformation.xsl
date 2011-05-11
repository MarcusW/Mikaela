<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pp="http://www-mmt.inf.tu-dresden.de/Lehre/Sommersemester_10/Vo_WME/Uebung/material/photonpainter" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:java="http://xml.apache.org/xalan/java" xmlns:javaHelper="photonCollector.TransformationHelper" version="2.0" exclude-result-prefixes="xhtml xsl xs java javaHelper">
<xsl:output method="xml" indent="yes" version="1.0" encoding="ISO-8859-1" cdata-section-elements="photocontainer"/> 

	<xsl:strip-space elements="*" />

	<xsl:template match="/">
		<pp>
			<xsl:apply-templates select="*" />
		</pp>
	</xsl:template>

	<xsl:template match="xhtml:head">
	</xsl:template>

	<xsl:template match="xhtml:body">
		<xsl:apply-templates select="*" />
	</xsl:template>

	<xsl:template match="xhtml:h1">
	</xsl:template>

	<xsl:template match="xhtml:ul">
		<xsl:apply-templates select="*" />
	</xsl:template>

	<xsl:template match="xhtml:li">
		<xsl:variable name="var_name">
			<xsl:value-of select="./xhtml:h2" />
		</xsl:variable>


		<!-- Pruefe ob das Bild bereits hochgeladen wurde. -->
		<xsl:choose>
			<!-- TODO: kleiner in groesser gleich aendern -->
			<xsl:when test="count(document('http://141.76.61.48:8103/photos')//pp:photo[@original_filename=$var_name]) &lt; 0">

			</xsl:when>
			<xsl:otherwise>
				<photocontainer>
					<xsl:variable name="var_url">
						<xsl:value-of select="./xhtml:img/@src" />
					</xsl:variable>
	
					<xsl:variable name="var_id" select="javaHelper:uploadImage($var_url)"/>
	
					<!-- Fuege neue id dem Photocontainer hinzu -->
					<xsl:attribute name="id">
						<xsl:value-of select="$var_id"/>
					</xsl:attribute>
					
					<xsl:variable name="var_date">
						<xsl:value-of select="string(./xhtml:dl/xhtml:dd[6])" />
					</xsl:variable>
	
					<xsl:variable name="var_created">
						<xsl:value-of select="javaHelper:dateToUnixTimestamp($var_date)" />
					</xsl:variable>
	
					<!-- GeoLat und GeoLong muessen aus Bilddatei ausgelesen werden -->
					<xsl:variable name="var_geo_lat">
						<xsl:value-of select="javaHelper:getMetaInformation('geo_lat', $var_url)" />
					</xsl:variable>
	
					<xsl:variable name="var_geo_long">
						<xsl:value-of select="javaHelper:getMetaInformation('geo_long', $var_url)" />
					</xsl:variable>
					
					<!-- gueltige user ID gdw Autor ist User -->
					<xsl:variable name="var_author">
							<xsl:value-of select="./xhtml:h3[2]" />
					</xsl:variable>
					
					<!-- Rufe ID des Users ab falls er im System registriert ist. Sonst setze UserID auf -1 -->
					<xsl:variable name="var_userID">
						<xsl:choose>
							<xsl:when test="count(document('http://141.76.61.48:8103/users')//pp:user[@username=$var_author]) &gt; 0">
								<xsl:value-of select="document('http://141.76.61.48:8103/users')//pp:user[@username=$var_author]/@id" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>-1</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<!-- Erstellen des Photo-Knotens falls der Upload erfolgreich war -->
					<xsl:if test="number($var_id) &gt;= 0">
						<photo>
							<!-- Pruefe ob die Zeit eine positive Zahl ist. Andernfalls trage 0 ein. -->
							<xsl:attribute name="created">
								<xsl:choose>
									<xsl:when test="number($var_created) &gt;= 0">
										<xsl:value-of select="$var_created" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>0</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							
							<!-- Fuer Title muss "Foto " aus dem alt-Attribut des img raus -->
							<xsl:attribute name="title">
								<xsl:value-of select="substring(./xhtml:img/@alt,6)" />
							</xsl:attribute>
							
							<!-- Falls es leerer String zurueckgeliefert wurde, muss das gesamt Attribut verschwinden -->
							<xsl:if test="string-length($var_geo_lat) &gt; 0"> 
								<xsl:attribute name="geo_lat"> 
									<xsl:value-of select="$var_geo_lat" /> 
								</xsl:attribute> 
							</xsl:if>
							
							<!-- Falls es leerer String zurueckgeliefert wurde, muss das gesamt Attribut verschwinden -->
							<xsl:if test="string-length($var_geo_long) &gt; 0">
								<xsl:attribute name="geo_long"> 
									<xsl:value-of select="$var_geo_long" /> 
								</xsl:attribute> 
							</xsl:if>
							
							<xsl:attribute name="aperture">
								<xsl:value-of select="./xhtml:dl/xhtml:dd[3]" />
							</xsl:attribute>
							
							<xsl:attribute name="exposuretime">
								<xsl:value-of select="./xhtml:dl/xhtml:dd[4]" />
							</xsl:attribute>
							
							<xsl:attribute name="focallength">
								<xsl:value-of select="./xhtml:dl/xhtml:dd[5]" />
							</xsl:attribute>
							
							<xsl:attribute name="user_name">
								<xsl:value-of select="./xhtml:h3[2]" />
							</xsl:attribute>
							
							<xsl:attribute name="upload_complete">
								<xsl:text>1</xsl:text>
							</xsl:attribute>
							
							<xsl:attribute name="author">
								<xsl:value-of select="$var_userID" />
							</xsl:attribute>
							<tags>
								<xsl:for-each select="./xhtml:dl/xhtml:dd[7]/xhtml:ul/xhtml:li">
									<tag>
										<xsl:value-of select="." />
									</tag>
								</xsl:for-each>
							</tags>
							<description>
								<xsl:value-of select="./xhtml:p" />
							</description>
						</photo>
					</xsl:if>

					<!-- Schreibe Log fuer dieses Bild -->
					<log>
						<xsl:attribute name="picture">
							<xsl:value-of select="$var_name" />
						</xsl:attribute>
						<xsl:if test="number($var_id) &lt; 0">
							<error>
								<xsl:text>Bild konnte nicht hochgeladen werden.</xsl:text>
							</error>
						</xsl:if>
						<xsl:if test="number($var_created) &lt; 0">
							<warning>
								<xsl:text>Die Zeit: </xsl:text>
								<xsl:value-of select="$var_date" />
								<xsl:text> konnte nicht in Unixzeit ueberfuehrt werden. Es wird der Wert 0 eingetragen.</xsl:text>
							</warning>
						</xsl:if>
						<xsl:if test="string-length($var_geo_long) &lt; 1">
							<warning>
								<xsl:text>Das Bild enthielt keine Informationen ueber den geografische Laenge oder die Umrechnung in Grad ist fehlgeschlagen.</xsl:text>
							</warning>						
						</xsl:if>	
						<xsl:if test="string-length($var_geo_lat) &lt; 1">
							<warning>
								<xsl:text>Das Bild enthielt keine Informationen ueber den geografische Breite oder die Umrechnung in Grad ist fehlgeschlagen.</xsl:text>
							</warning>						
						</xsl:if>
						<xsl:if test="number($var_userID) &lt; 0">
							<warning>
								<xsl:text>Die Nutzer: </xsl:text>
								<xsl:value-of select="$var_author" />
								<xsl:text> ist im System nicht registriert. Es wird -1 als author eingetragen.</xsl:text>
							</warning>
						</xsl:if>			
					</log>
				</photocontainer>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
