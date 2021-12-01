import sys
import json
import ast

# 1: input json file
# 2: output trec file
# 3: output reformatted json file

with open(sys.argv[1]) as f:
  with open(sys.argv[2], 'w') as w:
    with open(sys.argv[3], 'w') as j:
      for l in f:
        x = ast.literal_eval(l)
        if 'description' in x and 'title' in x:
          out = '<DOC>\n'
          out += '<DOCNO>' + x['asin'] + '</DOCNO>\n'
          out += '<HEAD>' + x['title'] + '</HEAD>\n'
          out += '<TEXT>\n'
          out += x['description'] + '\n'
          out += '</TEXT>\n'
          out += '</DOC>\n'
          w.write(out)
          j.write(json.dumps(x)+'\n')
