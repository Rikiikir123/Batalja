import os

def parse_txt_to_arff(txt_file, arff_file, write_header):
    fieldnames = ['turnsPlayed', 'survive', 'fleetGenerated', 'fleetLost', 'fleetReinforced', 'largestAttack', 'largestLoss', 'largestReinforcement', 'planetsLost', 'planetsConquered', 'planetsDefended', 'planetsAttacked', 'numFleetLost', 'numFleetReinforced', 'numFleetGenerated', 'totalTroopsGenerated']
    with open(txt_file, 'r') as txt, open(arff_file, 'a') as arff:
        # Write the header only once
        if write_header:
            arff.write('@RELATION stats\n')
            for field in fieldnames:
                if field == 'survive':
                    arff.write(f'@ATTRIBUTE {field} {{true, false}}\n')
                else:
                    arff.write(f'@ATTRIBUTE {field} NUMERIC\n')
            arff.write('@DATA\n')
        for line in txt:
            if line.startswith('STAT:'):
                stats = {}
                while not line.startswith('totalTroopsGenerated:'):
                    line = next(txt).strip()
                    key, value = line.split(': ')
                    stats[key] = value
                # Write the stats to the arff file
                arff.write(','.join([stats[field] for field in fieldnames]) + '\n')

# Directory containing the text files
txt_dir = 'C:/Users/rikis/Desktop/a/'
# Output ARFF file
arff_file = 'C:/Users/rikis/Desktop/output.arff'

# Process all text files in the directory
first_file = True
for filename in os.listdir(txt_dir):
    if filename.endswith('.txt'):
        txt_file = os.path.join(txt_dir, filename)
        parse_txt_to_arff(txt_file, arff_file, first_file)
        first_file = False
