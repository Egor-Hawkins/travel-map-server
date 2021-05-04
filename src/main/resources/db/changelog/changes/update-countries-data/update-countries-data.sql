UPDATE countries
SET name = CASE
    WHEN iso = 'BN' THEN 'Brunei'
    WHEN iso = 'CG' THEN 'Republic of the Congo'
    WHEN iso = 'CI' THEN 'Ivory Coast'
    WHEN iso = 'FK' THEN 'Falkland Islands'
    WHEN iso = 'PN' THEN 'Pitcairn Islands'
    WHEN iso = 'PS' THEN 'Palestine'
    WHEN iso = 'RU' THEN 'Russia'
    WHEN iso = 'SR' THEN 'Suriname'
    WHEN iso = 'SY' THEN 'Syria'
    WHEN iso = 'TL' THEN 'East Timor'
    WHEN iso = 'VG' THEN 'British Virgin Islands'
    WHEN iso = 'VI' THEN 'United States Virgin Islands'
    WHEN iso = 'VN' THEN 'Vietnam'
END
WHERE iso IN ('BN', 'CG', 'CI', 'FK', 'PN', 'PS', 'RU', 'SR', 'SY', 'TL', 'VG', 'VI', 'VN');

INSERT INTO countries (iso, name)
VALUES ('MC', 'Monaco');